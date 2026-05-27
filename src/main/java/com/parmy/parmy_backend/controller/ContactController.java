package com.parmy.parmy_backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parmy.parmy_backend.dto.ApiResponse;
import com.parmy.parmy_backend.dto.ContactRequest;
import com.parmy.parmy_backend.service.ContactService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = {"https://parmytechnologies.netlify.app", "http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class ContactController {
    
    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);
    
    @Autowired
    private ContactService contactService;
    
    /**
     * Handle contact form submissions
     * @param contactRequest the contact form data
     * @return ResponseEntity with success/error message
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> submitContactForm(@Valid @RequestBody ContactRequest contactRequest) {
        logger.info("Contact form submission received from: {} ({})", 
                   contactRequest.getFullName(), contactRequest.getEmail());
        
        try {
            // Send notification email to host
            contactService.sendContactFormEmail(contactRequest);
            
            // Send acknowledgment email to client (non-blocking)
            try {
                contactService.sendAcknowledgmentEmail(contactRequest);
            } catch (Exception e) {
                // Log but don't fail the main operation
                logger.warn("Acknowledgment email failed but contact form was processed successfully", e);
            }
            
            logger.info("Contact form processed successfully for: {}", contactRequest.getEmail());
            
            ApiResponse<String> response = ApiResponse.success(
                "Thank you for your message! We've received your inquiry and will get back to you within 24 hours."
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to process contact form from: {} ({})", 
                        contactRequest.getFullName(), contactRequest.getEmail(), e);
            
            ApiResponse<String> response = ApiResponse.error(
                "We're sorry, but there was an issue processing your message. Please try again or contact us directly at info@trivioglobal.com"
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Health check endpoint for contact service
     * @return ResponseEntity with service status
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Contact service is running"));
    }
    
    /**
     * Get available service options
     * @return ResponseEntity with service options
     */
    @GetMapping("/services")
    public ResponseEntity<ApiResponse<String[]>> getServices() {
        String[] services = {
            "software-development",
            "software-maintenance", 
            "seo",
            "digital-marketing",
            "ai-ml",
            "cloud",
            "consultancy",
            "other"
        };
        
        return ResponseEntity.ok(ApiResponse.success("Service options retrieved", services));
    }
    
    /**
     * Get available location options
     * @return ResponseEntity with location options
     */
    @GetMapping("/locations")
    public ResponseEntity<ApiResponse<String[]>> getLocations() {
        String[] locations = {
            "hyderabad",
            "seattle", 
            "portland",
            "jeddah",
            "other"
        };
        
        return ResponseEntity.ok(ApiResponse.success("Location options retrieved", locations));
    }
}