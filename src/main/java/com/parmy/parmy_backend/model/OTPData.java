package com.parmy.parmy_backend.model;

import java.time.LocalDateTime;

public class OTPData {
    
    private String email;
    private String otp;
    private LocalDateTime expiryTime;
    private OTPType type;
    private User temporaryUser; // For registration flow
    
    public OTPData() {}
    
    public OTPData(String email, String otp, OTPType type) {
        this.email = email;
        this.otp = otp;
        this.type = type;
        this.expiryTime = LocalDateTime.now().plusMinutes(10); // OTP expires in 10 minutes
    }
    
    public OTPData(String email, String otp, OTPType type, User temporaryUser) {
        this(email, otp, type);
        this.temporaryUser = temporaryUser;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getOtp() {
        return otp;
    }
    
    public void setOtp(String otp) {
        this.otp = otp;
    }
    
    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }
    
    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }
    
    public OTPType getType() {
        return type;
    }
    
    public void setType(OTPType type) {
        this.type = type;
    }
    
    public User getTemporaryUser() {
        return temporaryUser;
    }
    
    public void setTemporaryUser(User temporaryUser) {
        this.temporaryUser = temporaryUser;
    }
    
    @Override
    public String toString() {
        return "OTPData{" +
                "email='" + email + '\'' +
                ", type=" + type +
                ", expiryTime=" + expiryTime +
                ", expired=" + isExpired() +
                '}';
    }
}