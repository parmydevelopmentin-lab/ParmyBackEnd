package com.parmy.parmy_backend.repository;

import com.parmy.parmy_backend.model.Invoice;
import com.parmy.parmy_backend.model.InvoiceStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    
    /**
     * Find invoices by customer email
     * @param customerEmail the customer email
     * @return list of invoices
     */
    List<Invoice> findByCustomerEmailContainingIgnoreCase(String customerEmail);
    
    /**
     * Find invoices by project name
     * @param projectName the project name
     * @return list of invoices
     */
    List<Invoice> findByProjectNameContainingIgnoreCase(String projectName);
    
    /**
     * Find invoices by customer name
     * @param customerName the customer name
     * @return list of invoices
     */
    List<Invoice> findByCustomerNameContainingIgnoreCase(String customerName);
    
    /**
     * Find invoices by status
     * @param status the invoice status
     * @return list of invoices
     */
    List<Invoice> findByStatus(InvoiceStatus status);
    
    /**
     * Find invoices by status and ordered by creation date
     * @param status the invoice status
     * @return list of invoices ordered by creation date descending
     */
    List<Invoice> findByStatusOrderByCreatedAtDesc(InvoiceStatus status);
    
    /**
     * Find overdue invoices (due date passed and not paid)
     * @param currentDate the current date
     * @return list of overdue invoices
     */
    @Query("{ 'dueDate': { $lt: ?0 }, 'status': { $ne: 'PAID' } }")
    List<Invoice> findOverdueInvoices(LocalDate currentDate);
    
    /**
     * Find invoices created by a specific user
     * @param createdBy the user ID who created the invoices
     * @return list of invoices
     */
    List<Invoice> findByCreatedByOrderByCreatedAtDesc(String createdBy);
    
    /**
     * Find all invoices ordered by creation date (most recent first)
     * @return list of all invoices
     */
    List<Invoice> findAllByOrderByCreatedAtDesc();
    
    /**
     * Find invoices by invoice number
     * @param invoiceNumber the invoice number
     * @return list of invoices (should be unique)
     */
    List<Invoice> findByInvoiceNumberContainingIgnoreCase(String invoiceNumber);
    
    /**
     * Find invoices due within a specific range
     * @param startDate start date
     * @param endDate end date
     * @return list of invoices
     */
    List<Invoice> findByDueDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Count invoices by status
     * @param status the invoice status
     * @return count of invoices
     */
    long countByStatus(InvoiceStatus status);
    
    /**
     * Count overdue invoices
     * @param currentDate the current date
     * @return count of overdue invoices
     */
    @Query(value = "{ 'dueDate': { $lt: ?0 }, 'status': { $ne: 'PAID' } }", count = true)
    long countOverdueInvoices(LocalDate currentDate);
    
    /**
     * Find the latest invoice number for generating new invoice numbers
     * @return the latest invoice ordered by invoice number descending
     */
    Invoice findTopByOrderByInvoiceNumberDesc();
}