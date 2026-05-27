package com.parmy.parmy_backend.model;

public enum InvoiceStatus {
    PENDING("Pending"),
    PAID("Paid"),
    UNPAID("Unpaid"),
    OVERDUE("Overdue"),
    CANCELLED("Cancelled");
    
    private final String displayName;
    
    InvoiceStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}