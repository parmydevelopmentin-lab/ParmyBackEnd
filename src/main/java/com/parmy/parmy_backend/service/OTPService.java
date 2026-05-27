package com.parmy.parmy_backend.service;

import com.parmy.parmy_backend.model.OTPData;
import com.parmy.parmy_backend.model.OTPType;
import com.parmy.parmy_backend.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {
    
    private static final Logger logger = LoggerFactory.getLogger(OTPService.class);
    private static final int OTP_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();
    
    // Temporary storage for OTPs before verification
    private final ConcurrentHashMap<String, OTPData> otpStorage = new ConcurrentHashMap<>();
    
    /**
     * Generate a 6-digit OTP
     * @return OTP string
     */
    public String generateOTP() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
    
    /**
     * Store OTP for email verification
     * @param email the email
     * @param otp the OTP
     * @param type the OTP type
     */
    public void storeOTP(String email, String otp, OTPType type) {
        OTPData otpData = new OTPData(email, otp, type);
        otpStorage.put(email, otpData);
        logger.info("OTP stored for email: {} with type: {}", email, type);
    }
    
    /**
     * Store OTP for registration with temporary user data
     * @param email the email
     * @param otp the OTP
     * @param type the OTP type
     * @param temporaryUser the temporary user data
     */
    public void storeOTPWithUser(String email, String otp, OTPType type, User temporaryUser) {
        OTPData otpData = new OTPData(email, otp, type, temporaryUser);
        otpStorage.put(email, otpData);
        logger.info("OTP with user data stored for email: {} with type: {}", email, type);
    }
    
    /**
     * Verify OTP for given email
     * @param email the email
     * @param providedOTP the OTP provided by user
     * @return true if OTP is valid, false otherwise
     */
    public boolean verifyOTP(String email, String providedOTP) {
        OTPData storedOTPData = otpStorage.get(email);
        
        if (storedOTPData == null) {
            logger.warn("No OTP found for email: {}", email);
            return false;
        }
        
        if (storedOTPData.isExpired()) {
            logger.warn("OTP expired for email: {}", email);
            otpStorage.remove(email); // Clean up expired OTP
            return false;
        }
        
        boolean isValid = storedOTPData.getOtp().equals(providedOTP);
        if (isValid) {
            logger.info("OTP verified successfully for email: {}", email);
        } else {
            logger.warn("Invalid OTP provided for email: {}", email);
        }
        
        return isValid;
    }
    
    /**
     * Get OTP data for email (includes temporary user data if any)
     * @param email the email
     * @return OTPData object or null if not found
     */
    public OTPData getOTPData(String email) {
        return otpStorage.get(email);
    }
    
    /**
     * Remove OTP after successful verification
     * @param email the email
     */
    public void removeOTP(String email) {
        OTPData removed = otpStorage.remove(email);
        if (removed != null) {
            logger.info("OTP removed for email: {}", email);
        }
    }
    
    /**
     * Check if OTP exists for email
     * @param email the email
     * @return true if OTP exists, false otherwise
     */
    public boolean hasOTP(String email) {
        OTPData otpData = otpStorage.get(email);
        return otpData != null && !otpData.isExpired();
    }
    
    /**
     * Get remaining time for OTP in minutes
     * @param email the email
     * @return remaining time in minutes, -1 if no valid OTP
     */
    public long getRemainingTimeMinutes(String email) {
        OTPData otpData = otpStorage.get(email);
        if (otpData == null || otpData.isExpired()) {
            return -1;
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = otpData.getExpiryTime();
        return java.time.Duration.between(now, expiry).toMinutes();
    }
    
    /**
     * Clean up expired OTPs every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void cleanupExpiredOTPs() {
        int initialSize = otpStorage.size();
        otpStorage.entrySet().removeIf(entry -> entry.getValue().isExpired());
        int currentSize = otpStorage.size();
        
        if (initialSize > currentSize) {
            logger.info("Cleaned up {} expired OTPs. Current OTP storage size: {}", 
                       initialSize - currentSize, currentSize);
        }
    }
    
    /**
     * Get current OTP storage size (for monitoring)
     * @return current size of OTP storage
     */
    public int getStorageSize() {
        return otpStorage.size();
    }
    
    /**
     * Clear all OTPs (for testing or emergency cleanup)
     */
    public void clearAllOTPs() {
        int size = otpStorage.size();
        otpStorage.clear();
        logger.warn("All OTPs cleared. {} entries removed.", size);
    }
}