package com.parmy.parmy_backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "invoices")
public class Invoice {
    
    @Id
    private String id;
    
    private String invoiceNumber;
    
    // Customer Information
    private String customerName;
    private String customerEmail;
    
    // Project Information
    private String projectName;
    private Integer quantity;
    
    // Pricing Information
    private BigDecimal price; // Unit price
    private BigDecimal discountPercentage; // Discount percentage (0-100)
    private BigDecimal taxPercentage; // Tax percentage (0-100)
    
    // Calculated amounts
    private BigDecimal subtotal; // quantity * price
    private BigDecimal discountAmount; // subtotal * (discountPercentage / 100)
    private BigDecimal taxableAmount; // subtotal - discountAmount
    private BigDecimal taxAmount; // taxableAmount * (taxPercentage / 100)
    private BigDecimal totalAmount; // taxableAmount + taxAmount
    
    // Invoice Details
    private LocalDate dueDate;
    private InvoiceStatus status = InvoiceStatus.PENDING;
    
    // Audit Information
    private String createdBy; // User ID who created the invoice
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // PDF Information
    private String pdfPath; // Path to generated PDF file
    private boolean emailSent = false;
    
    // Constructors
    public Invoice() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Invoice(String customerName, String customerEmail, String projectName, 
                   Integer quantity, BigDecimal price, BigDecimal discountPercentage, 
                   BigDecimal taxPercentage, LocalDate dueDate, String createdBy) {
        this();
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.projectName = projectName;
        this.quantity = quantity;
        this.price = price;
        this.discountPercentage = discountPercentage;
        this.taxPercentage = taxPercentage;
        this.dueDate = dueDate;
        this.createdBy = createdBy;
        
        // Calculate amounts
        calculateAmounts();
    }
    
    /**
     * Calculate all amounts based on quantity, price, discount, and tax
     */
    public void calculateAmounts() {
        if (quantity != null && price != null && discountPercentage != null && taxPercentage != null) {
            // Calculate subtotal
            this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
            
            // Calculate discount amount
            this.discountAmount = subtotal.multiply(discountPercentage.divide(BigDecimal.valueOf(100)));
            
            // Calculate taxable amount (after discount)
            this.taxableAmount = subtotal.subtract(discountAmount);
            
            // Calculate tax amount
            this.taxAmount = taxableAmount.multiply(taxPercentage.divide(BigDecimal.valueOf(100)));
            
            // Calculate total amount
            this.totalAmount = taxableAmount.add(taxAmount);
        }
    }
    
    /**
     * Check if the invoice is overdue
     */
    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate) && status != InvoiceStatus.PAID;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateAmounts(); // Recalculate when quantity changes
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
        calculateAmounts(); // Recalculate when price changes
    }
    
    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
        calculateAmounts(); // Recalculate when discount changes
    }
    
    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }
    
    public void setTaxPercentage(BigDecimal taxPercentage) {
        this.taxPercentage = taxPercentage;
        calculateAmounts(); // Recalculate when tax changes
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public BigDecimal getTaxableAmount() {
        return taxableAmount;
    }
    
    public void setTaxableAmount(BigDecimal taxableAmount) {
        this.taxableAmount = taxableAmount;
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public InvoiceStatus getStatus() {
        return status;
    }
    
    public void setStatus(InvoiceStatus status) {
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
    
    public String getPdfPath() {
        return pdfPath;
    }
    
    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }
    
    public boolean isEmailSent() {
        return emailSent;
    }
    
    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }
    
    @Override
    public String toString() {
        return "Invoice{" +
                "id='" + id + '\'' +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", projectName='" + projectName + '\'' +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", dueDate=" + dueDate +
                ", overdue=" + isOverdue() +
                '}';
    }
}