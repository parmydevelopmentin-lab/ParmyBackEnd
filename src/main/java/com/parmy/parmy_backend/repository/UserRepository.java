package com.parmy.parmy_backend.repository;

import com.parmy.parmy_backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    /**
     * Find user by email
     * @param email the email to search for
     * @return Optional<User> containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by username
     * @param username the username to search for
     * @return Optional<User> containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by Google ID
     * @param googleId the Google ID to search for
     * @return Optional<User> containing the user if found
     */
    Optional<User> findByGoogleId(String googleId);
    
    /**
     * Check if user exists by email
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if user exists by username
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if user exists by Google ID
     * @param googleId the Google ID to check
     * @return true if user exists, false otherwise
     */
    boolean existsByGoogleId(String googleId);
}