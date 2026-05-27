package com.parmy.parmy_backend.controller;

import com.parmy.parmy_backend.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parmy.parmy_backend.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"https://parmytechnologies.netlify.app", "http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    /**
     * Register a new user
     * @param request the registration request
     * @return ResponseEntity with ApiResponse
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration attempt for email: {}", request.getEmail());
        
        ApiResponse<String> response = authService.register(request);
        
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Login user with email and password
     * @param request the login request
     * @return ResponseEntity with ApiResponse
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());
        
        ApiResponse<String> response = authService.login(request);
        
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Register user with Google OAuth
     * @param request the Google auth request
     * @return ResponseEntity with ApiResponse
     */
    @PostMapping("/google/register")
    public ResponseEntity<ApiResponse<String>> registerWithGoogle(@Valid @RequestBody GoogleAuthRequest request) {
        logger.info("Google registration attempt");
        
        ApiResponse<String> response = authService.registerWithGoogle(request);
        
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Login user with Google OAuth
     * @param request the Google auth request
     * @return ResponseEntity with ApiResponse
     */
    @PostMapping("/google/login")
    public ResponseEntity<ApiResponse<String>> loginWithGoogle(@Valid @RequestBody GoogleAuthRequest request) {
        logger.info("Google login attempt");
        
        ApiResponse<String> response = authService.loginWithGoogle(request);
        
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Send password reset code to user's email
     * @param request the forgot password request
     * @return ResponseEntity with ApiResponse
     */
    @PostMapping("/send-reset-code")
    public ResponseEntity<ApiResponse<String>> sendPasswordResetCode(@Valid @RequestBody ForgotPasswordRequest request) {
        logger.info("Password reset code request for email: {}", request.getEmail());
        
        ApiResponse<String> response = authService.sendPasswordResetCode(request);
        
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Verify password reset code
     * @param request the verify reset code request
     * @return ResponseEntity with ApiResponse
     */
    @PostMapping("/verify-reset-code")
    public ResponseEntity<ApiResponse<String>> verifyPasswordResetCode(@Valid @RequestBody VerifyResetCodeRequest request) {
        logger.info("Password reset code verification for email: {}", request.getEmail());
        
        ApiResponse<String> response = authService.verifyPasswordResetCode(request);
        
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Reset password with verified code
     * @param request the reset password request
     * @return ResponseEntity with ApiResponse
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        logger.info("Password reset request for email: {}", request.getEmail());
        
        ApiResponse<String> response = authService.resetPassword(request);
        
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Health check endpoint
     * @return ResponseEntity with success message
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Auth service is running"));
    }
}