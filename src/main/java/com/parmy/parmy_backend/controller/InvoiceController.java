package com.parmy.parmy_backend.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.parmy.parmy_backend.dto.ApiResponse;
import com.parmy.parmy_backend.dto.InvoiceRequest;
import com.parmy.parmy_backend.dto.InvoiceResponse;
import com.parmy.parmy_backend.model.InvoiceStatus;
import com.parmy.parmy_backend.model.User;
import com.parmy.parmy_backend.service.AuthService;
import com.parmy.parmy_backend.service.InvoiceService;
import com.parmy.parmy_backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = {"https://parmytechnologies.netlify.app", "http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class InvoiceController {
    
    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Create a new invoice (Admin only)
     * @param request the invoice request
     * @param httpRequest the HTTP request for token extraction
     * @return ResponseEntity with created invoice
     */
    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody InvoiceRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // Check admin authorization
            User currentUser = getCurrentUser(httpRequest);
            if (currentUser == null || !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                   .body(ApiResponse.error("Access denied. Admin privileges required."));
            }
            
            logger.info("Creating invoice for customer: {} by admin: {}", 
                       request.getCustomerEmail(), currentUser.getEmail());
            
            InvoiceResponse response = invoiceService.createInvoice(request, currentUser.getId());
            
            return ResponseEntity.ok(ApiResponse.success("Invoice created successfully", response));
            
        } catch (Exception e) {
            logger.error("Failed to create invoice", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(ApiResponse.error("Failed to create invoice: " + e.getMessage()));
        }
    }
    
    /**
     * Get all invoices (Admin only)
     * @param httpRequest the HTTP request for token extraction
     * @return ResponseEntity with list of invoices
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getAllInvoices(HttpServletRequest httpRequest) {
        try {
            // Check admin authorization
            User currentUser = getCurrentUser(httpRequest);
            if (currentUser == null || !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                   .body(ApiResponse.error("Access denied. Admin privileges required."));
            }
            
            List<InvoiceResponse> invoices = invoiceService.getAllInvoices();
            
            return ResponseEntity.ok(ApiResponse.success("Invoices retrieved successfully", invoices));
            
        } catch (Exception e) {
            logger.error("Failed to retrieve invoices", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(ApiResponse.error("Failed to retrieve invoices: " + e.getMessage()));
        }
    }
    
    /**
     * Get invoice by ID (Admin only)
     * @param id the invoice ID
     * @param httpRequest the HTTP request for token extraction
     * @return ResponseEntity with invoice
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        
        try {
            // Check admin authorization
            User currentUser = getCurrentUser(httpRequest);
            if (currentUser == null || !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                   .body(ApiResponse.error("Access denied. Admin privileges required."));
            }
            
            Optional<InvoiceResponse> invoice = invoiceService.getInvoiceById(id);
            
            if (invoice.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Invoice retrieved successfully", invoice.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                   .body(ApiResponse.error("Invoice not found"));
            }
            
        } catch (Exception e) {
            logger.error("Failed to retrieve invoice: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(ApiResponse.error("Failed to retrieve invoice: " + e.getMessage()));
        }
    }
    
    /**
     * Update invoice status (Admin only)
     * @param id the invoice ID
     * @param status the new status
     * @param httpRequest the HTTP request for token extraction
     * @return ResponseEntity with updated invoice
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<InvoiceResponse>> updateInvoiceStatus(
            @PathVariable String id,
            @RequestParam InvoiceStatus status,
            HttpServletRequest httpRequest) {
        
        try {
            // Check admin authorization
            User currentUser = getCurrentUser(httpRequest);
            if (currentUser == null || !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                   .body(ApiResponse.error("Access denied. Admin privileges required."));
            }
            
            Optional<InvoiceResponse> updatedInvoice = invoiceService.updateInvoiceStatus(id, status);
            
            if (updatedInvoice.isPresent()) {
                logger.info("Invoice status updated: {} -> {} by admin: {}", 
                           id, status, currentUser.getEmail());
                return ResponseEntity.ok(ApiResponse.success("Invoice status updated successfully", updatedInvoice.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                   .body(ApiResponse.error("Invoice not found"));
            }
            
        } catch (Exception e) {
            logger.error("Failed to update invoice status: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(ApiResponse.error("Failed to update invoice status: " + e.getMessage()));
        }
    }
    
    /**
     * Delete invoice (Admin only)
     * @param id the invoice ID
     * @param httpRequest the HTTP request for token extraction
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteInvoice(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        
        try {
            // Check admin authorization
            User currentUser = getCurrentUser(httpRequest);
            if (currentUser == null || !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                   .body(ApiResponse.error("Access denied. Admin privileges required."));
            }
            
            boolean deleted = invoiceService.deleteInvoice(id);
            
            if (deleted) {
                logger.info("Invoice deleted: {} by admin: {}", id, currentUser.getEmail());
                return ResponseEntity.ok(ApiResponse.success("Invoice deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                   .body(ApiResponse.error("Invoice not found"));
            }
            
        } catch (Exception e) {
            logger.error("Failed to delete invoice: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(ApiResponse.error("Failed to delete invoice: " + e.getMessage()));
        }
    }
    
    /**
     * Download invoice PDF (Admin only)
     * @param id the invoice ID
     * @param httpRequest the HTTP request for token extraction
     * @return ResponseEntity with PDF file
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadInvoicePDF(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        
        try {
            // Check admin authorization
            User currentUser = getCurrentUser(httpRequest);
            if (currentUser == null || !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                   .body(ApiResponse.error("Access denied. Admin privileges required."));
            }
            
            byte[] pdfBytes = invoiceService.downloadInvoicePDF(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice_" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            logger.info("Invoice PDF downloaded: {} by admin: {}", id, currentUser.getEmail());
            
            return ResponseEntity.ok()
                               .headers(headers)
                               .body(pdfBytes);
            
        } catch (DocumentException | IOException e) {
            logger.error("Failed to generate PDF for invoice: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(ApiResponse.error("Failed to generate PDF: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Failed to download invoice PDF: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(ApiResponse.error("Failed to download invoice: " + e.getMessage()));
        }
    }
    
    /**
     * Resend invoice PDF (Admin only)
     * @param id the invoice ID
     * @param httpRequest the HTTP request for token extraction
     * @return ResponseEntity with success message
     */
    @PostMapping("/{id}/resend")
    public ResponseEntity<ApiResponse<String>> resendInvoicePDF(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        
        try {
            // Check admin authorization
            User currentUser = getCurrentUser(httpRequest);
            if (currentUser == null || !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                   .body(ApiResponse.error("Access denied. Admin privileges required."));
            }
            
            boolean sent = invoiceService.resendInvoicePDF(id);
            
            if (sent) {
                logger.info("Invoice PDF resent: {} by admin: {}", id, currentUser.getEmail());
                return ResponseEntity.ok(ApiResponse.success("Invoice PDF sent successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                   .body(ApiResponse.error("Failed to send invoice PDF"));
            }
            
        } catch (Exception e) {
            logger.error("Failed to resend invoice PDF: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(ApiResponse.error("Failed to resend invoice: " + e.getMessage()));
        }
    }
    
    /**
     * Filter invoices by status (Admin only)
     * @param status the invoice status
     * @param httpRequest the HTTP request for token extraction
     * @return ResponseEntity with filtered invoices
     */
    @GetMapping("/filter/status/{status}")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getInvoicesByStatus(
            @PathVariable InvoiceStatus status,
            HttpServletRequest httpRequest) {
        
        try {
            // Check admin authorization
            User currentUser = getCurrentUser(httpRequest);
            if (currentUser == null || !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                   .body(ApiResponse.error("Access denied. Admin privileges required."));
            }
            
            List<InvoiceResponse> invoices = invoiceService.getInvoicesByStatus(status);
            
            return ResponseEntity.ok(ApiResponse.success("Filtered invoices retrieved successfully", invoices));
            
        } catch (Exception e) {
            logger.error("Failed to filter invoices by status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(ApiResponse.error("Failed to filter invoices: " + e.getMessage()));
        }
    }
    
    /**
     * Get overdue invoices (Admin only)
     * @param httpRequest the HTTP request for token extraction
     * @return ResponseEntity with overdue invoices
     */
    @GetMapping("/filter/overdue")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getOverdueInvoices(HttpServletRequest httpRequest) {
        try {
            // Check admin authorization
            User currentUser = getCurrentUser(httpRequest);
            if (currentUser == null || !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                   .body(ApiResponse.error("Access denied. Admin privileges required."));
            }
            
            List<InvoiceResponse> invoices = invoiceService.getOverdueInvoices();
            
            return ResponseEntity.ok(ApiResponse.success("Overdue invoices retrieved successfully", invoices));
            
        } catch (Exception e) {
            logger.error("Failed to retrieve overdue invoices", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(ApiResponse.error("Failed to retrieve overdue invoices: " + e.getMessage()));
        }
    }
    
    /**
     * Search invoices (Admin only)
     * @param type the search type (email, project, customer)
     * @param query the search query
     * @param httpRequest the HTTP request for token extraction
     * @return ResponseEntity with search results
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> searchInvoices(
            @RequestParam String type,
            @RequestParam String query,
            HttpServletRequest httpRequest) {
        
        try {
            // Check admin authorization
            User currentUser = getCurrentUser(httpRequest);
            if (currentUser == null || !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                   .body(ApiResponse.error("Access denied. Admin privileges required."));
            }
            
            List<InvoiceResponse> invoices;
            
            switch (type.toLowerCase()) {
                case "email":
                    invoices = invoiceService.searchByCustomerEmail(query);
                    break;
                case "project":
                    invoices = invoiceService.searchByProjectName(query);
                    break;
                case "customer":
                    invoices = invoiceService.searchByCustomerName(query);
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                       .body(ApiResponse.error("Invalid search type. Use: email, project, or customer"));
            }
            
            return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", invoices));
            
        } catch (Exception e) {
            logger.error("Failed to search invoices", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(ApiResponse.error("Failed to search invoices: " + e.getMessage()));
        }
    }
    
    /**
     * Get invoice statistics (Admin only)
     * @param httpRequest the HTTP request for token extraction
     * @return ResponseEntity with invoice statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<InvoiceService.InvoiceStats>> getInvoiceStats(HttpServletRequest httpRequest) {
        try {
            // Check admin authorization
            User currentUser = getCurrentUser(httpRequest);
            if (currentUser == null || !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                   .body(ApiResponse.error("Access denied. Admin privileges required."));
            }
            
            InvoiceService.InvoiceStats stats = invoiceService.getInvoiceStats();
            
            return ResponseEntity.ok(ApiResponse.success("Invoice statistics retrieved successfully", stats));
            
        } catch (Exception e) {
            logger.error("Failed to retrieve invoice statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(ApiResponse.error("Failed to retrieve statistics: " + e.getMessage()));
        }
    }
    
    /**
     * Extract current user from JWT token
     * @param request the HTTP request
     * @return the current user or null if not authenticated
     */
    private User getCurrentUser(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = jwtUtil.extractEmail(token);
                
                if (email != null && jwtUtil.validateToken(token)) {
                    return authService.getUserByEmail(email);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to extract user from token", e);
        }
        
        return null;
    }
}