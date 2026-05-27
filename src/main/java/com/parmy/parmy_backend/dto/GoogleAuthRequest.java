package com.parmy.parmy_backend.dto;

import jakarta.validation.constraints.NotBlank;

public class GoogleAuthRequest {
    
    @NotBlank(message = "Token is required")
    private String token;
    
    // Constructors
    public GoogleAuthRequest() {}
    
    public GoogleAuthRequest(String token) {
        this.token = token;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    @Override
    public String toString() {
        return "GoogleAuthRequest{" +
                "token='[PROTECTED]'" +
                '}';
    }
}