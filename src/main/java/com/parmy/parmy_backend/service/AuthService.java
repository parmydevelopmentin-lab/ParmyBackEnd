package com.parmy.parmy_backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.parmy.parmy_backend.dto.*;
import com.parmy.parmy_backend.model.*;
import com.parmy.parmy_backend.repository.UserRepository;
import com.parmy.parmy_backend.repository.PasswordResetTokenRepository;
import com.parmy.parmy_backend.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private OTPService otpService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    
    private static final int MAX_RESET_ATTEMPTS_PER_HOUR = 3;
    
    /**
     * Register a new user with email verification
     * @param request the registration request
     * @return ApiResponse indicating success or failure
     */
    public ApiResponse<String> register(RegisterRequest request) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                return ApiResponse.error("Email already registered");
            }
            
            // Check if username already exists
            if (userRepository.existsByUsername(request.getUsername())) {
                return ApiResponse.error("Username already taken");
            }
            
            // Create temporary user (not saved to DB yet)
            User temporaryUser = new User(
                request.getEmail(),
                request.getUsername(),
                passwordEncoder.encode(request.getPassword())
            );
            
            // Generate and send OTP
            String otp = otpService.generateOTP();
            otpService.storeOTPWithUser(request.getEmail(), otp, OTPType.REGISTRATION, temporaryUser);
            emailService.sendOTPEmail(request.getEmail(), otp, true);
            
            logger.info("Registration OTP sent to email: {}", request.getEmail());
            return ApiResponse.success("OTP sent to your email. Please verify to complete registration.");
            
        } catch (Exception e) {
            logger.error("Registration failed for email: {}", request.getEmail(), e);
            return ApiResponse.error("Registration failed. Please try again.");
        }
    }
    
    /**
     * Login user with email and password
     * @param request the login request
     * @return ApiResponse indicating success or failure
     */
    public ApiResponse<AuthResponse> login(LoginRequest request) {
        try {
            // Find user by email
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isEmpty()) {
                return ApiResponse.error("Invalid email or password");
            }
            
            User user = userOpt.get();
            
            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ApiResponse.error("Invalid email or password");
            }
            
            // Check if user is verified
            if (!user.isVerified()) {
                return ApiResponse.error("Please verify your email first");
            }
            
            // Generate JWT token directly (no OTP needed for login)
            String token = jwtUtil.generateToken(user.getId(), user.getEmail());
            AuthResponse authResponse = new AuthResponse(token, new UserResponse(user));
            
            logger.info("User logged in successfully (direct): {}", request.getEmail());
            return ApiResponse.success("Login successful", authResponse);
            
        } catch (Exception e) {
            logger.error("Login failed for email: {}", request.getEmail(), e);
            return ApiResponse.error("Login failed. Please try again.");
        }
    }
    
    /**
     * Verify OTP and complete registration or login
     * @param request the OTP verification request
     * @return ApiResponse with AuthResponse containing token and user info
     */
    public ApiResponse<AuthResponse> verifyOTP(OTPRequest request) {
        try {
            // Verify OTP
            if (!otpService.verifyOTP(request.getEmail(), request.getOtp())) {
                return ApiResponse.error("Invalid or expired OTP");
            }
            
            // Get OTP data to determine the type and get user info
            OTPData otpData = otpService.getOTPData(request.getEmail());
            if (otpData == null) {
                return ApiResponse.error("OTP verification failed");
            }
            
            User user;
            boolean isNewUser = false;
            
            // Handle based on OTP type
            switch (otpData.getType()) {
                case REGISTRATION:
                case GOOGLE_REGISTRATION:
                    // Complete registration
                    user = otpData.getTemporaryUser();
                    if (user == null) {
                        return ApiResponse.error("Registration data not found");
                    }
                    user.setVerified(true);
                    user = userRepository.save(user);
                    isNewUser = true;
                    logger.info("User registered successfully: {}", user.getEmail());
                    break;
                    
                case LOGIN:
                case GOOGLE_LOGIN:
                    // Complete login
                    Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
                    if (userOpt.isEmpty()) {
                        return ApiResponse.error("User not found");
                    }
                    user = userOpt.get();
                    user.setUpdatedAt(LocalDateTime.now());
                    user = userRepository.save(user);
                    logger.info("User logged in successfully: {}", user.getEmail());
                    break;
                    
                default:
                    return ApiResponse.error("Invalid OTP type");
            }
            
            // Remove OTP after successful verification
            otpService.removeOTP(request.getEmail());
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user.getId(), user.getEmail());
            
            // Create response
            AuthResponse authResponse = new AuthResponse(token, new UserResponse(user));
            
            // Send welcome email for new users
            if (isNewUser) {
                try {
                    emailService.sendRegistrationSuccessEmail(user.getEmail(), user.getUsername());
                } catch (Exception e) {
                    logger.warn("Failed to send welcome email to: {}", user.getEmail(), e);
                    // Don't fail the whole operation for email sending failure
                }
            }
            
            return ApiResponse.success(
                isNewUser ? "Registration completed successfully" : "Login successful",
                authResponse
            );
            
        } catch (Exception e) {
            logger.error("OTP verification failed for email: {}", request.getEmail(), e);
            return ApiResponse.error("OTP verification failed. Please try again.");
        }
    }
    
    /**
     * Register user with Google OAuth
     * @param request the Google auth request
     * @return ApiResponse indicating success or failure
     */
    public ApiResponse<String> registerWithGoogle(GoogleAuthRequest request) {
        try {
            // Verify Google token and get user info
            GoogleIdToken.Payload payload = verifyGoogleToken(request.getToken());
            if (payload == null) {
                return ApiResponse.error("Invalid Google token");
            }
            
            String email = payload.getEmail();
            String googleId = payload.getSubject();
            String name = (String) payload.get("name");
            
            // Check if user already exists
            if (userRepository.existsByEmail(email)) {
                return ApiResponse.error("Email already registered");
            }
            
            // Create temporary user for Google registration
            User temporaryUser = new User(email, name, googleId, AuthProvider.GOOGLE);
            
            // Generate and send OTP
            String otp = otpService.generateOTP();
            otpService.storeOTPWithUser(email, otp, OTPType.GOOGLE_REGISTRATION, temporaryUser);
            emailService.sendGoogleOTPEmail(email, otp, true);
            
            logger.info("Google registration OTP sent to email: {}", email);
            return ApiResponse.success("OTP sent to your email. Please verify to complete Google registration.");
            
        } catch (Exception e) {
            logger.error("Google registration failed", e);
            return ApiResponse.error("Google registration failed. Please try again.");
        }
    }
    
    /**
     * Login user with Google OAuth
     * @param request the Google auth request
     * @return ApiResponse indicating success or failure
     */
    public ApiResponse<AuthResponse> loginWithGoogle(GoogleAuthRequest request) {
        try {
            // Verify Google token and get user info
            GoogleIdToken.Payload payload = verifyGoogleToken(request.getToken());
            if (payload == null) {
                return ApiResponse.error("Invalid Google token");
            }
            
            String email = payload.getEmail();
            
            // Check if user exists
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ApiResponse.error("No account found with this email. Please register first.");
            }
            
            User user = userOpt.get();
            
            // Check if user was registered with Google; if not, link it automatically since email is verified by Google
            if (user.getAuthProvider() != AuthProvider.GOOGLE) {
                user.setAuthProvider(AuthProvider.GOOGLE);
                String googleId = payload.getSubject();
                user.setGoogleId(googleId);
                userRepository.save(user);
                logger.info("Linked existing local user account to Google OAuth: {}", email);
            }
            
            // Generate JWT token directly (no OTP needed for Google login)
            String token = jwtUtil.generateToken(user.getId(), user.getEmail());
            AuthResponse authResponse = new AuthResponse(token, new UserResponse(user));
            
            logger.info("Google user logged in successfully (direct): {}", email);
            return ApiResponse.success("Login successful", authResponse);
            
        } catch (Exception e) {
            logger.error("Google login failed", e);
            return ApiResponse.error("Google login failed. Please try again.");
        }
    }
    
    /**
     * Verify Google ID token
     * @param idTokenString the Google ID token
     * @return GoogleIdToken.Payload if valid, null otherwise
     */
    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), 
                GsonFactory.getDefaultInstance()
            )
            .setAudience(Collections.singletonList(googleClientId))
            .build();
            
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                return idToken.getPayload();
            }
            return null;
            
        } catch (Exception e) {
            logger.error("Google token verification failed", e);
            return null;
        }
    }
    
    /**
     * Get user by email
     * @param email the email
     * @return User if found, null otherwise
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    /**
     * Get user by ID
     * @param userId the user ID
     * @return User if found, null otherwise
     */
    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }
    
    /**
     * Send password reset code to user's email
     * @param request the forgot password request
     * @return ApiResponse indicating success or failure
     */
    public ApiResponse<String> sendPasswordResetCode(ForgotPasswordRequest request) {
        try {
            String email = request.getEmail();
            
            // Check if user exists
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                // Don't reveal that user doesn't exist for security reasons
                return ApiResponse.success("If an account with this email exists, a reset code has been sent.");
            }
            
            User user = userOpt.get();
            
            // Check if user is verified
            if (!user.isVerified()) {
                return ApiResponse.error("Please verify your email address first before resetting password.");
            }
            
            // Check rate limiting - max 3 attempts per hour
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            long recentAttempts = passwordResetTokenRepository.countByEmailAndCreatedAtGreaterThanEqual(email, oneHourAgo);
            if (recentAttempts >= MAX_RESET_ATTEMPTS_PER_HOUR) {
                return ApiResponse.error("Too many password reset attempts. Please try again after an hour.");
            }
            
            // Check if there's already a valid token
            LocalDateTime now = LocalDateTime.now();
            if (passwordResetTokenRepository.existsValidTokenByEmail(email, now)) {
                return ApiResponse.error("A password reset code was already sent. Please check your email or wait before requesting a new one.");
            }
            
            // Generate 6-digit reset code
            String resetCode = generateResetCode();
            
            // Invalidate any existing tokens for this email
            passwordResetTokenRepository.deleteByEmail(email);
            
            // Create and save new password reset token
            PasswordResetToken resetToken = new PasswordResetToken(email, resetCode);
            passwordResetTokenRepository.save(resetToken);
            
            // Send password reset email
            try {
                emailService.sendPasswordResetEmail(email, user.getUsername(), resetCode);
                logger.info("Password reset code sent to email: {}", email);
            } catch (Exception emailError) {
                logger.error("Email sending failed for: {}", email, emailError);
                // Clean up the token if email failed
                passwordResetTokenRepository.deleteByEmail(email);
                throw emailError; // Re-throw to be caught by outer catch
            }
            
            return ApiResponse.success("If an account with this email exists, a reset code has been sent.");
            
        } catch (Exception e) {
            logger.error("Failed to send password reset code for email: {} - Error: {}", request.getEmail(), e.getMessage(), e);
            return ApiResponse.error("Failed to send reset code. Please try again.");
        }
    }
    
    /**
     * Verify password reset code
     * @param request the verify reset code request
     * @return ApiResponse indicating success or failure
     */
    public ApiResponse<String> verifyPasswordResetCode(VerifyResetCodeRequest request) {
        try {
            String email = request.getEmail();
            String resetCode = request.getResetCode();
            
            // Find valid token
            LocalDateTime now = LocalDateTime.now();
            Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository
                .findValidTokenByEmailAndResetCode(email, resetCode, now);
            
            if (tokenOpt.isEmpty()) {
                return ApiResponse.error("Invalid or expired reset code.");
            }
            
            PasswordResetToken token = tokenOpt.get();
            
            // Check if token is still valid
            if (!token.isValid()) {
                return ApiResponse.error("Reset code has expired or been used.");
            }
            
            // Check if max attempts reached
            if (token.isMaxAttemptsReached()) {
                return ApiResponse.error("Maximum verification attempts reached. Please request a new reset code.");
            }
            
            // Token is valid - don't mark as used yet, just verify
            logger.info("Password reset code verified for email: {}", email);
            return ApiResponse.success("Reset code verified successfully. You can now set your new password.");
            
        } catch (Exception e) {
            logger.error("Failed to verify password reset code for email: {}", request.getEmail(), e);
            return ApiResponse.error("Failed to verify reset code. Please try again.");
        }
    }
    
    /**
     * Reset password with verified code
     * @param request the reset password request
     * @return ApiResponse indicating success or failure
     */
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {
        try {
            String email = request.getEmail();
            String resetCode = request.getResetCode();
            String newPassword = request.getNewPassword();
            
            // Validate password confirmation
            if (!request.isPasswordMatching()) {
                return ApiResponse.error("Passwords do not match.");
            }
            
            // Find valid token
            LocalDateTime now = LocalDateTime.now();
            Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository
                .findValidTokenByEmailAndResetCode(email, resetCode, now);
            
            if (tokenOpt.isEmpty()) {
                return ApiResponse.error("Invalid or expired reset code.");
            }
            
            PasswordResetToken token = tokenOpt.get();
            
            // Check if token is still valid
            if (!token.isValid()) {
                return ApiResponse.error("Reset code has expired or been used.");
            }
            
            // Find user
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ApiResponse.error("User not found.");
            }
            
            User user = userOpt.get();
            
            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Mark token as used
            token.markAsUsed();
            passwordResetTokenRepository.save(token);
            
            // Send password changed notification email
            try {
                emailService.sendPasswordChangedEmail(email, user.getUsername());
            } catch (Exception e) {
                logger.warn("Failed to send password changed notification to: {}", email, e);
                // Don't fail the whole operation for email sending failure
            }
            
            logger.info("Password reset successfully for email: {}", email);
            return ApiResponse.success("Password has been reset successfully. You can now log in with your new password.");
            
        } catch (Exception e) {
            logger.error("Failed to reset password for email: {}", request.getEmail(), e);
            return ApiResponse.error("Failed to reset password. Please try again.");
        }
    }
    
    /**
     * Generate a 6-digit numeric reset code
     * @return String containing the reset code
     */
    private String generateResetCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
    
    /**
     * Clean up expired password reset tokens
     * This method can be called periodically to clean up the database
     */
    public void cleanupExpiredTokens() {
        try {
            LocalDateTime now = LocalDateTime.now();
            passwordResetTokenRepository.deleteExpiredTokens(now);
            logger.info("Cleaned up expired password reset tokens");
        } catch (Exception e) {
            logger.error("Failed to cleanup expired password reset tokens", e);
        }
    }
}