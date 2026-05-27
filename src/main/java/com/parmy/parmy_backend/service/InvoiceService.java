package com.parmy.parmy_backend.service;

import com.itextpdf.text.DocumentException;
import com.parmy.parmy_backend.dto.InvoiceRequest;
import com.parmy.parmy_backend.dto.InvoiceResponse;
import com.parmy.parmy_backend.model.Invoice;
import com.parmy.parmy_backend.model.InvoiceStatus;
import com.parmy.parmy_backend.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PDFService pdfService;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Create a new invoice
     * 
     * @param request   the invoice request
     * @param createdBy the user ID who created the invoice
     * @return the created invoice response
     */
    public InvoiceResponse createInvoice(InvoiceRequest request, String createdBy) {
        try {
            // Create invoice from request
            Invoice invoice = new Invoice(
                    request.getCustomerName(),
                    request.getCustomerEmail(),
                    request.getProjectName(),
                    request.getQuantity(),
                    request.getPrice(),
                    request.getDiscountPercentage(),
                    request.getTaxPercentage(),
                    request.getDueDate(),
                    createdBy);

            // Generate invoice number
            invoice.setInvoiceNumber(generateInvoiceNumber());

            // Save invoice to database
            invoice = invoiceRepository.save(invoice);

            // Generate and send PDF
            try {
                sendInvoicePDF(invoice);
                invoice.setEmailSent(true);
                invoice = invoiceRepository.save(invoice);
                logger.info("Invoice PDF sent successfully for invoice: {}", invoice.getInvoiceNumber());
            } catch (Exception e) {
                logger.error("Failed to send invoice PDF for invoice: {}", invoice.getInvoiceNumber(), e);
                // Don't fail the whole operation, just log the error
            }

            logger.info("Invoice created successfully: {}", invoice.getInvoiceNumber());
            return new InvoiceResponse(invoice);

        } catch (Exception e) {
            logger.error("Failed to create invoice for customer: {}", request.getCustomerEmail(), e);
            throw new RuntimeException("Failed to create invoice", e);
        }
    }

    /**
     * Get all invoices
     * 
     * @return list of invoice responses
     */
    public List<InvoiceResponse> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAllByOrderByCreatedAtDesc();
        return invoices.stream()
                .map(InvoiceResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get invoice by ID
     * 
     * @param id the invoice ID
     * @return the invoice response
     */
    public Optional<InvoiceResponse> getInvoiceById(String id) {
        return invoiceRepository.findById(id)
                .map(InvoiceResponse::new);
    }

    /**
     * Update invoice status
     * 
     * @param id     the invoice ID
     * @param status the new status
     * @return the updated invoice response
     */
    public Optional<InvoiceResponse> updateInvoiceStatus(String id, InvoiceStatus status) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(id);

        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            invoice.setStatus(status);
            invoice.setUpdatedAt(LocalDateTime.now());

            invoice = invoiceRepository.save(invoice);
            logger.info("Invoice status updated: {} -> {}", invoice.getInvoiceNumber(), status);

            return Optional.of(new InvoiceResponse(invoice));
        }

        return Optional.empty();
    }

    /**
     * Delete invoice
     * 
     * @param id the invoice ID
     * @return true if deleted, false if not found
     */
    public boolean deleteInvoice(String id) {
        if (invoiceRepository.existsById(id)) {
            invoiceRepository.deleteById(id);
            logger.info("Invoice deleted: {}", id);
            return true;
        }
        return false;
    }

    /**
     * Filter invoices by status
     * 
     * @param status the invoice status
     * @return list of filtered invoice responses
     */
    public List<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status) {
        List<Invoice> invoices = invoiceRepository.findByStatusOrderByCreatedAtDesc(status);
        return invoices.stream()
                .map(InvoiceResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get overdue invoices
     * 
     * @return list of overdue invoice responses
     */
    public List<InvoiceResponse> getOverdueInvoices() {
        List<Invoice> invoices = invoiceRepository.findOverdueInvoices(LocalDate.now());
        return invoices.stream()
                .map(InvoiceResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Search invoices by customer email
     * 
     * @param email the customer email
     * @return list of matching invoice responses
     */
    public List<InvoiceResponse> searchByCustomerEmail(String email) {
        List<Invoice> invoices = invoiceRepository.findByCustomerEmailContainingIgnoreCase(email);
        return invoices.stream()
                .map(InvoiceResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Search invoices by project name
     * 
     * @param projectName the project name
     * @return list of matching invoice responses
     */
    public List<InvoiceResponse> searchByProjectName(String projectName) {
        List<Invoice> invoices = invoiceRepository.findByProjectNameContainingIgnoreCase(projectName);
        return invoices.stream()
                .map(InvoiceResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Search invoices by customer name
     * 
     * @param customerName the customer name
     * @return list of matching invoice responses
     */
    public List<InvoiceResponse> searchByCustomerName(String customerName) {
        List<Invoice> invoices = invoiceRepository.findByCustomerNameContainingIgnoreCase(customerName);
        return invoices.stream()
                .map(InvoiceResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Generate PDF for invoice download
     * 
     * @param id the invoice ID
     * @return PDF as byte array
     */
    public byte[] downloadInvoicePDF(String id) throws DocumentException, IOException {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(id);

        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            return pdfService.generateInvoicePDF(invoice);
        }

        throw new RuntimeException("Invoice not found with ID: " + id);
    }

    /**
     * Send invoice PDF via email
     * 
     * @param invoice the invoice to send
     * @throws MessagingException if email sending fails
     * @throws DocumentException  if PDF generation fails
     * @throws IOException        if PDF generation fails
     */
    public void sendInvoicePDF(Invoice invoice) throws MessagingException, DocumentException, IOException {
        // Generate PDF
        byte[] pdfBytes = pdfService.generateInvoicePDF(invoice);

        // Create email message
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(invoice.getCustomerEmail());
        helper.setSubject("Invoice " + invoice.getInvoiceNumber() + " from PARMY TECHNOLOGIES");

        // Email body
        String emailBody = buildInvoiceEmailBody(invoice);
        helper.setText(emailBody, true); // HTML email

        // Add PDF attachment
        ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfBytes, "application/pdf");
        helper.addAttachment("Invoice_" + invoice.getInvoiceNumber() + ".pdf", dataSource);

        // Send email
        mailSender.send(message);
        logger.info("Invoice PDF sent to: {} for invoice: {}", invoice.getCustomerEmail(), invoice.getInvoiceNumber());
    }

    /**
     * Resend invoice PDF
     * 
     * @param id the invoice ID
     * @return true if sent successfully
     */
    public boolean resendInvoicePDF(String id) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(id);

        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            try {
                sendInvoicePDF(invoice);
                invoice.setEmailSent(true);
                invoiceRepository.save(invoice);
                return true;
            } catch (Exception e) {
                logger.error("Failed to resend invoice PDF for invoice: {}", invoice.getInvoiceNumber(), e);
                return false;
            }
        }

        return false;
    }

    /**
     * Get invoice statistics
     * 
     * @return invoice statistics
     */
    public InvoiceStats getInvoiceStats() {
        long totalInvoices = invoiceRepository.count();
        long paidInvoices = invoiceRepository.countByStatus(InvoiceStatus.PAID);
        long unpaidInvoices = invoiceRepository.countByStatus(InvoiceStatus.UNPAID);
        long pendingInvoices = invoiceRepository.countByStatus(InvoiceStatus.PENDING);
        long overdueInvoices = invoiceRepository.countOverdueInvoices(LocalDate.now());

        return new InvoiceStats(totalInvoices, paidInvoices, unpaidInvoices, pendingInvoices, overdueInvoices);
    }

    /**
     * Generate unique invoice number
     * 
     * @return generated invoice number
     */
    private String generateInvoiceNumber() {
        String prefix = "INV-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-";

        // Get the latest invoice number
        Invoice latestInvoice = invoiceRepository.findTopByOrderByInvoiceNumberDesc();

        int nextNumber = 1;
        if (latestInvoice != null && latestInvoice.getInvoiceNumber() != null) {
            String latestNumber = latestInvoice.getInvoiceNumber();
            try {
                // Extract the number part after the last dash
                String numberPart = latestNumber.substring(latestNumber.lastIndexOf("-") + 1);
                nextNumber = Integer.parseInt(numberPart) + 1;
            } catch (Exception e) {
                logger.warn("Failed to parse latest invoice number: {}", latestNumber, e);
                nextNumber = 1;
            }
        }

        return prefix + String.format("%04d", nextNumber);
    }

    /**
     * Build email body for invoice
     * 
     * @param invoice the invoice
     * @return HTML email body
     */
    private String buildInvoiceEmailBody(Invoice invoice) {
        StringBuilder body = new StringBuilder();

        body.append("<html><body style='font-family: Arial, sans-serif; color: #333;'>");
        body.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>");

        // Header
        body.append("<div style='text-align: center; margin-bottom: 30px;'>");
        body.append("<h1 style='color: #1A202C; margin-bottom: 5px;'>PARMY TECHNOLOGIES</h1>");
        body.append(
                "<p style='color: #34A853; font-size: 14px; margin: 0;'>Transforming Ideas into Digital Reality</p>");
        body.append("</div>");

        // Greeting
        body.append("<h2 style='color: #1A202C;'>Dear ").append(invoice.getCustomerName()).append(",</h2>");

        body.append("<p>Thank you for your business! Please find attached your invoice for the project: <strong>")
                .append(invoice.getProjectName()).append("</strong></p>");

        // Invoice details
        body.append("<div style='background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0;'>");
        body.append("<h3 style='color: #1A202C; margin-top: 0;'>Invoice Details:</h3>");
        body.append("<p><strong>Invoice Number:</strong> ").append(invoice.getInvoiceNumber()).append("</p>");
        body.append("<p><strong>Due Date:</strong> ")
                .append(invoice.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))).append("</p>");
        body.append("<p><strong>Amount Due:</strong> ₹").append(String.format("%,.2f", invoice.getTotalAmount()))
                .append("</p>");
        body.append("<p><strong>Status:</strong> ").append(invoice.getStatus().getDisplayName()).append("</p>");
        body.append("</div>");

        // Payment instructions
        body.append("<h3 style='color: #1A202C;'>Payment Instructions:</h3>");
        body.append("<p>Please make payment within 30 days of the due date. You can pay via:</p>");
        body.append("<ul>");
        body.append("<li>Bank transfer</li>");
        body.append("<li>Online payment portal</li>");
        body.append("<li>Check payable to PARMY TECHNOLOGIES PVT LTD</li>");
        body.append("</ul>");

        // Contact info
        body.append("<div style='border-top: 1px solid #e5e7eb; padding-top: 20px; margin-top: 30px;'>");
        body.append("<p>If you have any questions about this invoice, please contact us:</p>");
        body.append("<p>");
        body.append("📧 Email: info@parmytechnologies.com<br>");
        body.append("📞 Phone: +91 81252 45777<br>");
        body.append("🌐 Website: https://parmytechnologies.com");
        body.append("</p>");
        body.append("</div>");

        // Footer
        body.append(
                "<div style='text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #e5e7eb;'>");
        body.append("<p style='color: #6b7280; font-size: 12px;'>");
        body.append("This is an automated email. Please do not reply directly to this message.<br>");
        body.append("© 2024 PARMY TECHNOLOGIES. All rights reserved.");
        body.append("</p>");
        body.append("</div>");

        body.append("</div></body></html>");

        return body.toString();
    }

    /**
     * Inner class for invoice statistics
     */
    public static class InvoiceStats {
        private final long totalInvoices;
        private final long paidInvoices;
        private final long unpaidInvoices;
        private final long pendingInvoices;
        private final long overdueInvoices;

        public InvoiceStats(long totalInvoices, long paidInvoices, long unpaidInvoices,
                long pendingInvoices, long overdueInvoices) {
            this.totalInvoices = totalInvoices;
            this.paidInvoices = paidInvoices;
            this.unpaidInvoices = unpaidInvoices;
            this.pendingInvoices = pendingInvoices;
            this.overdueInvoices = overdueInvoices;
        }

        // Getters
        public long getTotalInvoices() {
            return totalInvoices;
        }

        public long getPaidInvoices() {
            return paidInvoices;
        }

        public long getUnpaidInvoices() {
            return unpaidInvoices;
        }

        public long getPendingInvoices() {
            return pendingInvoices;
        }

        public long getOverdueInvoices() {
            return overdueInvoices;
        }
    }
}