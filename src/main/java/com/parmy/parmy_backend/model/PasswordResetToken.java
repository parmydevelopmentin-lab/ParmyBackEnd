package com.parmy.parmy_backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

/**
 * Model for password reset tokens with TTL (Time-To-Live) functionality
 * Automatically removed from MongoDB after expiration
 */
@Document(collection = "password_reset_tokens")
public class PasswordResetToken {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    private String resetCode;
    
    private LocalDateTime createdAt;
    
    @Indexed(expireAfterSeconds = 600) // TTL: 10 minutes (600 seconds)
    private LocalDateTime expiresAt;
    
    private boolean used;
    
    private int attemptCount;
    
    private static final int MAX_ATTEMPTS = 5;
    private static final int EXPIRY_MINUTES = 10;
    
    // Default constructor
    public PasswordResetToken() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(EXPIRY_MINUTES);
        this.used = false;
        this.attemptCount = 0;
    }
    
    // Parameterized constructor
    public PasswordResetToken(String email, String resetCode) {
        this();
        this.email = email;
        this.resetCode = resetCode;
    }
    
    /**
     * Check if the token is expired
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * Check if the token is valid for use
     * @return true if valid (not expired, not used, not exceeded max attempts)
     */
    public boolean isValid() {
        return !isExpired() && !used && attemptCount < MAX_ATTEMPTS;
    }
    
    /**
     * Mark the token as used
     */
    public void markAsUsed() {
        this.used = true;
    }
    
    /**
     * Increment the attempt count
     */
    public void incrementAttempts() {
        this.attemptCount++;
    }
    
    /**
     * Check if max attempts reached
     * @return true if max attempts reached
     */
    public boolean isMaxAttemptsReached() {
        return attemptCount >= MAX_ATTEMPTS;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }
    
    @Override
    public String toString() {
        return "PasswordResetToken{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", resetCode='[HIDDEN]'" +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", used=" + used +
                ", attemptCount=" + attemptCount +
                ", expired=" + isExpired() +
                ", valid=" + isValid() +
                '}';
    }
}