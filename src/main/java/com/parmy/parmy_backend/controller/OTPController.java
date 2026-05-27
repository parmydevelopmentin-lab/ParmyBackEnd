package com.parmy.parmy_backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parmy.parmy_backend.dto.ApiResponse;
import com.parmy.parmy_backend.dto.AuthResponse;
import com.parmy.parmy_backend.dto.OTPRequest;
import com.parmy.parmy_backend.service.AuthService;
import com.parmy.parmy_backend.service.OTPService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin(origins = {"https://parmytechnologies.netlify.app", "http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class OTPController {
    
    private static final Logger logger = LoggerFactory.getLogger(OTPController.class);
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private OTPService otpService;
    
    /**
     * Verify OTP and complete registration or login
     * @param request the OTP verification request
     * @return ResponseEntity with ApiResponse containing AuthResponse
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOTP(@Valid @RequestBody OTPRequest request) {
        logger.info("OTP verification attempt for email: {}", request.getEmail());
        
        ApiResponse<AuthResponse> response = authService.verifyOTP(request);
        
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Check if OTP exists for email (for frontend validation)
     * @param email the email to check
     * @return ResponseEntity with ApiResponse indicating if OTP exists
     */
    @GetMapping("/status/{email}")
    public ResponseEntity<ApiResponse<OTPStatusResponse>> getOTPStatus(@PathVariable String email) {
        logger.info("OTP status check for email: {}", email);
        
        boolean hasOTP = otpService.hasOTP(email);
        long remainingTime = otpService.getRemainingTimeMinutes(email);
        
        OTPStatusResponse status = new OTPStatusResponse(hasOTP, remainingTime);
        
        return ResponseEntity.ok(ApiResponse.success("OTP status retrieved", status));
    }
    
    /**
     * Health check endpoint
     * @return ResponseEntity with success message
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("OTP service is running"));
    }
    
    /**
     * Inner class for OTP status response
     */
    public static class OTPStatusResponse {
        private boolean hasOTP;
        private long remainingTimeMinutes;
        
        public OTPStatusResponse(boolean hasOTP, long remainingTimeMinutes) {
            this.hasOTP = hasOTP;
            this.remainingTimeMinutes = remainingTimeMinutes;
        }
        
        // Getters and Setters
        public boolean isHasOTP() {
            return hasOTP;
        }
        
        public void setHasOTP(boolean hasOTP) {
            this.hasOTP = hasOTP;
        }
        
        public long getRemainingTimeMinutes() {
            return remainingTimeMinutes;
        }
        
        public void setRemainingTimeMinutes(long remainingTimeMinutes) {
            this.remainingTimeMinutes = remainingTimeMinutes;
        }
    }
}