package com.parmy.parmy_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class OfferRequest {
    @NotBlank(message = "Candidate name is required")
    private String candidateName;
    
    @NotBlank(message = "Candidate email is required")
    @Email(message = "Please provide a valid email address")
    private String candidateEmail;
    
    @NotBlank(message = "Role is required")
    private String role;
    
    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotBlank(message = "Trial period is required")
    private String trialPeriod;
    
    private String address; // Optional - will use default if not provided
    
    // Constructors
    public OfferRequest() {}
    
    public OfferRequest(String candidateName, String candidateEmail, String role, 
                      LocalDate joiningDate, String location, String trialPeriod, String address) {
        this.candidateName = candidateName;
        this.candidateEmail = candidateEmail;
        this.role = role;
        this.joiningDate = joiningDate;
        this.location = location;
        this.trialPeriod = trialPeriod;
        this.address = address;
    }
    
    // Getters and Setters
    public String getCandidateName() {
        return candidateName;
    }
    
    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }
    
    public String getCandidateEmail() {
        return candidateEmail;
    }
    
    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public LocalDate getJoiningDate() {
        return joiningDate;
    }
    
    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getTrialPeriod() {
        return trialPeriod;
    }
    
    public void setTrialPeriod(String trialPeriod) {
        this.trialPeriod = trialPeriod;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    @Override
    public String toString() {
        return "OfferRequest{" +
                "candidateName='" + candidateName + '\'' +
                ", candidateEmail='" + candidateEmail + '\'' +
                ", role='" + role + '\'' +
                ", joiningDate=" + joiningDate +
                ", location='" + location + '\'' +
                ", trialPeriod='" + trialPeriod + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}