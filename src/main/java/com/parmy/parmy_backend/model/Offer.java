package com.parmy.parmy_backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "offers")
public class Offer {
    @Id
    private String id;
    
    @Indexed
    private String candidateName;
    
    @Indexed
    private String candidateEmail;
    
    private String role;
    private LocalDate joiningDate;
    private String location;
    private String trialPeriod;
    private String address;
    
    @Indexed
    private OfferStatus status;
    
    private String createdBy; // Admin email who created the offer
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime sentAt;
    
    // PDF file details
    private String pdfFileName;
    private String pdfFilePath;
    
    // Email tracking
    private boolean emailSent;
    private LocalDateTime emailSentAt;
    private String emailSubject;
    
    // Constructors
    public Offer() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = OfferStatus.DRAFT;
        this.emailSent = false;
    }
    
    public Offer(String candidateName, String candidateEmail, String role, LocalDate joiningDate, 
                String location, String trialPeriod, String address, String createdBy) {
        this();
        this.candidateName = candidateName;
        this.candidateEmail = candidateEmail;
        this.role = role;
        this.joiningDate = joiningDate;
        this.location = location;
        this.trialPeriod = trialPeriod;
        this.address = address;
        this.createdBy = createdBy;
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
        this.updatedAt = LocalDateTime.now();
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
    
    public String getPdfFileName() {
        return pdfFileName;
    }
    
    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }
    
    public String getPdfFilePath() {
        return pdfFilePath;
    }
    
    public void setPdfFilePath(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }
    
    public boolean isEmailSent() {
        return emailSent;
    }
    
    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
        if (emailSent) {
            this.emailSentAt = LocalDateTime.now();
        }
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
    
    // Helper methods
    public void markAsSent() {
        this.status = OfferStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.emailSent = true;
        this.emailSentAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Offer{" +
                "id='" + id + '\'' +
                ", candidateName='" + candidateName + '\'' +
                ", candidateEmail='" + candidateEmail + '\'' +
                ", role='" + role + '\'' +
                ", joiningDate=" + joiningDate +
                ", location='" + location + '\'' +
                ", status=" + status +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}