package com.parmy.parmy_backend.dto;

import com.parmy.parmy_backend.model.Expense;
import com.parmy.parmy_backend.model.ExpenseCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExpenseResponse {
    private String id;
    private String title;
    private ExpenseCategory category;
    private double amount;
    private LocalDate expenseDate;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ExpenseResponse() {}

    public ExpenseResponse(Expense expense) {
        this.id = expense.getId();
        this.title = expense.getTitle();
        this.category = expense.getCategory();
        this.amount = expense.getAmount();
        this.expenseDate = expense.getExpenseDate();
        this.notes = expense.getNotes();
        this.createdBy = expense.getCreatedBy();
        this.createdAt = expense.getCreatedAt();
        this.updatedAt = expense.getUpdatedAt();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    @Override
    public String toString() {
        return "ExpenseResponse{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", amount=" + amount +
                ", expenseDate=" + expenseDate +
                ", notes='" + notes + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}