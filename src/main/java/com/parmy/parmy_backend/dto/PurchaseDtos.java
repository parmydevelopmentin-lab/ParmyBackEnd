package com.parmy.parmy_backend.dto;

import java.time.LocalDateTime;

import com.parmy.parmy_backend.model.PurchaseStatus;

public class PurchaseDtos {
    public static class CreatePurchaseRequest {
        public String projectId;
        public String notes; // optional
    }

    public static class PurchaseResponse {
        public String id;
        public String userId;
        public String username;
        public String userEmail;
        public String projectId;
        public String projectTitle;
        public double projectPrice;
        public String projectCurrency;
        public PurchaseStatus status;
        public String proofFilePath; // for internal use
        public String proofFileName; // just the filename
        public String proofUrl; // full URL to access the file
        public String notes;
        public LocalDateTime createdAt;
        public LocalDateTime updatedAt;
    }
}
