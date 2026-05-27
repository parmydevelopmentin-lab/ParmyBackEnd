package com.parmy.parmy_backend.dto;

import com.parmy.parmy_backend.model.OfferStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class OfferResponse {
    private String id;
    private String candidateName;
    private String candidateEmail;
    private String role;
    private LocalDate joiningDate;
    private String location;
    private String trialPeriod;
    private String address;
    private OfferStatus status;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime sentAt;
    private boolean emailSent;
    private LocalDateTime emailSentAt;
    private String emailSubject;
    private String pdfFileName;
    
    // Constructors
    public OfferResponse() {}
    
    public OfferResponse(String id, String candidateName, String candidateEmail, String role,
                        LocalDate joiningDate, String location, String trialPeriod, String address,
                        OfferStatus status, String createdBy, LocalDateTime createdAt,
                        LocalDateTime updatedAt, LocalDateTime sentAt, boolean emailSent,
                        LocalDateTime emailSentAt, String emailSubject, String pdfFileName) {
        this.id = id;
        this.candidateName = candidateName;
        this.candidateEmail = candidateEmail;
        this.role = role;
        this.joiningDate = joiningDate;
        this.location = location;
        this.trialPeriod = trialPeriod;
        this.address = address;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.sentAt = sentAt;
        this.emailSent = emailSent;
        this.emailSentAt = emailSentAt;
        this.emailSubject = emailSubject;
        this.pdfFileName = pdfFileName;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
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
    
    public OfferStatus getStatus() {
        return status;
    }
    
    public void setStatus(OfferStatus status) {
        this.status = status;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public boolean isEmailSent() {
        return emailSent;
    }
    
    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }
    
    public LocalDateTime getEmailSentAt() {
        return emailSentAt;
    }
    
    public void setEmailSentAt(LocalDateTime emailSentAt) {
        this.emailSentAt = emailSentAt;
    }
    
    public String getEmailSubject() {
        return emailSubject;
    }
    
    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }
    
    public String getPdfFileName() {
        return pdfFileName;
    }
    
    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }
    
    @Override
    public String toString() {
        return "OfferResponse{" +
                "id='" + id + '\'' +
                ", candidateName='" + candidateName + '\'' +
                ", candidateEmail='" + candidateEmail + '\'' +
                ", role='" + role + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}