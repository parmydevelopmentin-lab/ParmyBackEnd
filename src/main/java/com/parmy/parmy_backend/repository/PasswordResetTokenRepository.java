package com.parmy.parmy_backend.repository;

import com.parmy.parmy_backend.model.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for PasswordResetToken entity
 * Provides database operations for password reset functionality
 */
@Repository
public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    
    /**
     * Find the most recent valid password reset token by email
     * @param email the email to search for
     * @return Optional<PasswordResetToken> containing the token if found
     */
    Optional<PasswordResetToken> findByEmailAndUsedFalseOrderByCreatedAtDesc(String email);
    
    /**
     * Find password reset token by email and reset code
     * @param email the email to search for
     * @param resetCode the reset code to match
     * @return Optional<PasswordResetToken> containing the token if found
     */
    Optional<PasswordResetToken> findByEmailAndResetCode(String email, String resetCode);
    
    /**
     * Find valid password reset token by email and reset code
     * @param email the email to search for
     * @param resetCode the reset code to match
     * @return Optional<PasswordResetToken> containing the token if valid
     */
    @Query("{'email': ?0, 'resetCode': ?1, 'used': false, 'expiresAt': {$gte: ?2}}")
    Optional<PasswordResetToken> findValidTokenByEmailAndResetCode(String email, String resetCode, LocalDateTime currentTime);
    
    /**
     * Delete all password reset tokens for a specific email
     * @param email the email to delete tokens for
     */
    void deleteByEmail(String email);
    
    /**
     * Delete all expired password reset tokens
     * @param currentTime the current time to compare against
     */
    @Query(value = "{'expiresAt': {$lt: ?0}}", delete = true)
    void deleteExpiredTokens(LocalDateTime currentTime);
    
    /**
     * Check if a valid password reset token exists for the given email
     * @param email the email to check
     * @param currentTime the current time to compare against
     * @return true if a valid token exists, false otherwise
     */
    default boolean existsValidTokenByEmail(String email, LocalDateTime currentTime) {
        return findByEmailAndUsedFalseOrderByCreatedAtDesc(email)
            .map(token -> !token.isExpired() && token.isValid())
            .orElse(false);
    }
    
    /**
     * Count the number of password reset attempts for an email in the last hour
     * @param email the email to check
     * @param oneHourAgo the time one hour ago
     * @return the count of attempts
     */
    long countByEmailAndCreatedAtGreaterThanEqual(String email, LocalDateTime oneHourAgo);
}