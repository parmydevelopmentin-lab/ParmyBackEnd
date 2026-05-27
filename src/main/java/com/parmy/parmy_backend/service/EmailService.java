package com.parmy.parmy_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send OTP email to user
     * 
     * @param email          recipient email
     * @param otp            the OTP code
     * @param isRegistration true if for registration, false if for login
     */
    public void sendOTPEmail(String email, String otp, boolean isRegistration) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);

            if (isRegistration) {
                message.setSubject("PARMY TECHNOLOGIES PVT LTD - Email Verification OTP");
                message.setText(buildRegistrationOTPMessage(otp));
            } else {
                message.setSubject("PARMY TECHNOLOGIES PVT LTD - Login Verification OTP");
                message.setText(buildLoginOTPMessage(otp));
            }

            mailSender.send(message);
            logger.info("OTP email sent successfully to: {}", email);

        } catch (Exception e) {
            logger.error("Failed to send OTP email to: {}", email, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    /**
     * Send Google OAuth OTP email to user
     * 
     * @param email          recipient email
     * @param otp            the OTP code
     * @param isRegistration true if for registration, false if for login
     */
    public void sendGoogleOTPEmail(String email, String otp, boolean isRegistration) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);

            if (isRegistration) {
                message.setSubject("PARMY TECHNOLOGIES PVT LTD - Google Account Verification OTP");
                message.setText(buildGoogleRegistrationOTPMessage(otp));
            } else {
                message.setSubject("PARMY TECHNOLOGIES PVT LTD - Google Login Verification OTP");
                message.setText(buildGoogleLoginOTPMessage(otp));
            }

            mailSender.send(message);
            logger.info("Google OTP email sent successfully to: {}", email);

        } catch (Exception e) {
            logger.error("Failed to send Google OTP email to: {}", email, e);
            throw new RuntimeException("Failed to send Google OTP email", e);
        }
    }

    /**
     * Send registration success email
     * 
     * @param email    recipient email
     * @param username the username
     */
    public void sendRegistrationSuccessEmail(String email, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Welcome to PARMY TECHNOLOGIES PVT LTD - Registration Successful!");
            message.setText(buildRegistrationSuccessMessage(username));

            mailSender.send(message);
            logger.info("Registration success email sent successfully to: {}", email);

        } catch (Exception e) {
            logger.error("Failed to send registration success email to: {}", email, e);
            // Don't throw exception here as registration is already complete
            logger.warn("Registration completed but success email failed for: {}", email);
        }
    }

    /**
     * Build registration OTP message
     * 
     * @param otp the OTP code
     * @return formatted message
     */
    private String buildRegistrationOTPMessage(String otp) {
        return String.format(
                "Welcome to PARMY!\n\n" +
                        "Your email verification OTP is: %s\n\n" +
                        "This OTP will expire in 10 minutes.\n" +
                        "Please enter this code to complete your registration.\n\n" +
                        "If you didn't request this, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "PARMY Team",
                otp);
    }

    /**
     * Build login OTP message
     * 
     * @param otp the OTP code
     * @return formatted message
     */
    private String buildLoginOTPMessage(String otp) {
        return String.format(
                "PARMY TECHNOLOGIES PVT LTD Login Verification\n\n" +
                        "Your login verification OTP is: %s\n\n" +
                        "This OTP will expire in 10 minutes.\n" +
                        "Please enter this code to complete your login.\n\n" +
                        "If you didn't request this, please secure your account immediately.\n\n" +
                        "Best regards,\n" +
                        "PARMY Team",
                otp);
    }

    /**
     * Build Google registration OTP message
     * 
     * @param otp the OTP code
     * @return formatted message
     */
    private String buildGoogleRegistrationOTPMessage(String otp) {
        return String.format(
                "Welcome to PARMY TECHNOLOGIES PVT LTD!\n\n" +
                        "Your Google account verification OTP is: %s\n\n" +
                        "This OTP will expire in 10 minutes.\n" +
                        "Please enter this code to complete your Google account registration.\n\n" +
                        "If you didn't request this, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "PARMY Team",
                otp);
    }

    /**
     * Build Google login OTP message
     * 
     * @param otp the OTP code
     * @return formatted message
     */
    private String buildGoogleLoginOTPMessage(String otp) {
        return String.format(
                "PARMY TECHNOLOGIES PVT LTD Google Login Verification\n\n" +
                        "Your Google login verification OTP is: %s\n\n" +
                        "This OTP will expire in 10 minutes.\n" +
                        "Please enter this code to complete your Google login.\n\n" +
                        "If you didn't request this, please secure your account immediately.\n\n" +
                        "Best regards,\n" +
                        "PARMY Team",
                otp);
    }

    /**
     * Build registration success message
     * 
     * @param username the username
     * @return formatted message
     */
    private String buildRegistrationSuccessMessage(String username) {
        return String.format(
                "Welcome to PARMY TECHNOLOGIES PVT LTD, %s!\n\n" +
                        "Your registration has been completed successfully.\n" +
                        "You can now access all features of our platform.\n\n" +
                        "Thank you for joining us!\n\n" +
                        "Best regards,\n" +
                        "PARMY Team",
                username);
    }

    /**
     * Send password reset email to user
     * 
     * @param email     recipient email
     * @param username  the username
     * @param resetCode the 6-digit reset code
     */
    public void sendPasswordResetEmail(String email, String username, String resetCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("PARMY TECHNOLOGIES PVT LTD - Password Reset Request");
            message.setText(buildPasswordResetMessage(username, resetCode));

            mailSender.send(message);
            logger.info("Password reset email sent successfully to: {}", email);

        } catch (Exception e) {
            logger.error("Failed to send password reset email to: {}", email, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    /**
     * Send password changed notification email
     * 
     * @param email    recipient email
     * @param username the username
     */
    public void sendPasswordChangedEmail(String email, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("PARMY TECHNOLOGIES PVT LTD - Password Changed Successfully");
            message.setText(buildPasswordChangedMessage(username));

            mailSender.send(message);
            logger.info("Password changed notification email sent successfully to: {}", email);

        } catch (Exception e) {
            logger.error("Failed to send password changed notification email to: {}", email, e);
            // Don't throw exception here as password reset is already complete
            logger.warn("Password changed but notification email failed for: {}", email);
        }
    }

    /**
     * Send admin notification for a new purchase submission
     */
    public void sendPurchaseSubmittedNotification(String adminEmail, String buyerEmail, String projectTitle,
            double amount, String currency) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject("New Purchase Submitted - " + projectTitle);
            message.setText(String.format(
                    "A new purchase has been submitted.\n\nBuyer: %s\nProject: %s\nAmount: %.2f %s\n\nPlease review and verify the payment proof in the admin dashboard.",
                    buyerEmail, projectTitle, amount, currency));
            mailSender.send(message);
            logger.info("Purchase submitted notification sent to admin: {}", adminEmail);
        } catch (Exception e) {
            logger.error("Failed to send purchase notification to admin: {}", adminEmail, e);
        }
    }

    /**
     * Send confirmation email to buyer after submitting purchase
     */
    public void sendPurchaseConfirmationToBuyer(String buyerEmail, String projectTitle) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(buyerEmail);
            message.setSubject("Purchase Submitted - " + projectTitle);
            message.setText(String.format(
                    "Thank you for your purchase submission for '%s'.\n\nOur team will verify your payment proof shortly and notify you once it's approved.",
                    projectTitle));
            mailSender.send(message);
            logger.info("Purchase submission confirmation sent to buyer: {}", buyerEmail);
        } catch (Exception e) {
            logger.error("Failed to send purchase confirmation to buyer: {}", buyerEmail, e);
        }
    }

    /**
     * Build password reset message
     * 
     * @param username  the username
     * @param resetCode the reset code
     * @return formatted message
     */
    private String buildPasswordResetMessage(String username, String resetCode) {
        return String.format(
                "Hello %s,\n\n" +
                        "We received a request to reset your password for your PARMY account.\n\n" +
                        "Your password reset code is: %s\n\n" +
                        "This code will expire in 10 minutes for security reasons.\n" +
                        "Please enter this code on the password reset page to set your new password.\n\n" +
                        "If you didn't request this password reset, please ignore this email.\n" +
                        "Your account is still secure and no changes have been made.\n\n" +
                        "For security reasons:\n" +
                        "• Never share this code with anyone\n" +
                        "• PARMY staff will never ask for this code\n" +
                        "• If you suspect suspicious activity, contact our support team\n\n" +
                        "Best regards,\n" +
                        "PARMY Security Team",
                username,
                resetCode);
    }

    /**
     * Build password changed notification message
     * 
     * @param username the username
     * @return formatted message
     */
    private String buildPasswordChangedMessage(String username) {
        return String.format(
                "Hello %s,\n\n" +
                        "This is to confirm that your password for your PARMY account has been successfully changed.\n\n"
                        +
                        "If you made this change, no further action is required.\n\n" +
                        "If you did NOT make this change:\n" +
                        "• Your account may have been compromised\n" +
                        "• Please contact our support team immediately\n" +
                        "• Consider enabling additional security measures\n\n" +
                        "Account security tips:\n" +
                        "• Use a strong, unique password\n" +
                        "• Enable two-factor authentication when available\n" +
                        "• Don't share your login credentials\n" +
                        "• Log out from shared devices\n\n" +
                        "If you have any concerns about your account security, please contact us immediately.\n\n" +
                        "Best regards,\n" +
                        "PARMY Security Team",
                username);
    }
}