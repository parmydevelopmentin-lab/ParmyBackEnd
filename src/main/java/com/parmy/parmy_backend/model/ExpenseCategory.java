package com.parmy.parmy_backend.model;

public enum ExpenseCategory {
    SALARY("Salary"),
    HARDWARE("Hardware"),
    SOFTWARE("Software"),
    OFFICE_RENT("Office Rent"),
    UTILITIES("Utilities"),
    TRAVEL("Travel"),
    MARKETING("Marketing"),
    TRAINING("Training"),
    LEGAL("Legal"),
    INSURANCE("Insurance"),
    MAINTENANCE("Maintenance"),
    MISCELLANEOUS("Miscellaneous");

    private final String displayName;

    ExpenseCategory(String displayName) {
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