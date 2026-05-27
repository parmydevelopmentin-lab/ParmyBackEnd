package com.parmy.parmy_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for verifying password reset code
 */
public class VerifyResetCodeRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;
    
    @NotBlank(message = "Reset code is required")
    @Size(min = 6, max = 6, message = "Reset code must be exactly 6 digits")
    @Pattern(regexp = "\\d{6}", message = "Reset code must contain only digits")
    private String resetCode;
    
    // Default constructor
    public VerifyResetCodeRequest() {}
    
    // Parameterized constructor
    public VerifyResetCodeRequest(String email, String resetCode) {
        this.email = email;
        this.resetCode = resetCode;
    }
    
    // Getters and Setters
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
    
    @Override
    public String toString() {
        return "VerifyResetCodeRequest{" +
                "email='" + email + '\'' +
                ", resetCode='[HIDDEN]'" +
                '}';
    }
}