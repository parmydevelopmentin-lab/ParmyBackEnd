package com.parmy.parmy_backend.dto;

import com.parmy.parmy_backend.model.AuthProvider;
import com.parmy.parmy_backend.model.User;
import com.parmy.parmy_backend.model.UserRole;

import java.time.LocalDateTime;
import java.util.Set;

public class UserResponse {
    
    private String id;
    private String email;
    private String username;
    private boolean verified;
    private AuthProvider authProvider;
    private Set<UserRole> roles;
    private LocalDateTime createdAt;
    
    // Constructors
    public UserResponse() {}
    
    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.verified = user.isVerified();
        this.authProvider = user.getAuthProvider();
        this.roles = user.getRoles();
        this.createdAt = user.getCreatedAt();
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
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public boolean isVerified() {
        return verified;
    }
    
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    
    public AuthProvider getAuthProvider() {
        return authProvider;
    }
    
    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }
    
    public Set<UserRole> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}