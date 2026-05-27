package com.parmy.parmy_backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

import com.parmy.parmy_backend.dto.ApiResponse;
import com.parmy.parmy_backend.dto.OfferRequest;
import com.parmy.parmy_backend.dto.OfferResponse;
import com.parmy.parmy_backend.model.OfferStatus;
import com.parmy.parmy_backend.service.OfferService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/offers")
@CrossOrigin(origins = {"https://parmytechnologies.netlify.app", "http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
@PreAuthorize("hasRole('ADMIN')") // All endpoints require admin role
public class OfferController {

    private static final Logger logger = LoggerFactory.getLogger(OfferController.class);

    @Autowired
    private OfferService offerService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OfferResponse>> createOffer(
            @Valid @RequestBody OfferRequest request,
            Authentication authentication) {
        try {
            logger.info("Creating offer for candidate: {}", request.getCandidateEmail());

            String adminEmail = authentication.getName();
            OfferResponse response = offerService.createOffer(request, adminEmail);

            ApiResponse<OfferResponse> apiResponse = new ApiResponse<>(
                    true,
                    "Offer created successfully",
                    response
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

        } catch (Exception e) {
            logger.error("Error creating offer", e);
            ApiResponse<OfferResponse> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to create offer: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<ApiResponse<OfferResponse>> sendOffer(
            @PathVariable String id,
            Authentication authentication) {
        try {
            logger.info("Sending offer with ID: {}", id);

            String adminEmail = authentication.getName();
            OfferResponse response = offerService.sendOffer(id, adminEmail);

            ApiResponse<OfferResponse> apiResponse = new ApiResponse<>(
                    true,
                    "Offer sent successfully to candidate",
                    response
            );

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            logger.error("Error sending offer: " + id, e);
            ApiResponse<OfferResponse> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to send offer: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/{id}/resend")
    public ResponseEntity<ApiResponse<OfferResponse>> resendOffer(
            @PathVariable String id,
            Authentication authentication) {
        try {
            logger.info("Resending offer with ID: {}", id);

            String adminEmail = authentication.getName();
            OfferResponse response = offerService.resendOffer(id, adminEmail);

            ApiResponse<OfferResponse> apiResponse = new ApiResponse<>(
                    true,
                    "Offer resent successfully to candidate",
                    response
            );

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            logger.error("Error resending offer: " + id, e);
            ApiResponse<OfferResponse> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to resend offer: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<OfferResponse>>> getAllOffers(Authentication authentication) {
        try {
            logger.info("Fetching all offers");

            String adminEmail = authentication.getName();
            List<OfferResponse> offers = offerService.getAllOffers(adminEmail);

            ApiResponse<List<OfferResponse>> apiResponse = new ApiResponse<>(
                    true,
                    "Offers retrieved successfully",
                    offers
            );

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            logger.error("Error fetching offers", e);
            ApiResponse<List<OfferResponse>> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to fetch offers: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<OfferResponse>>> getOffersByStatus(
            @PathVariable OfferStatus status,
            Authentication authentication) {
        try {
            logger.info("Fetching offers with status: {}", status);

            String adminEmail = authentication.getName();
            List<OfferResponse> offers = offerService.getOffersByStatus(status, adminEmail);

            ApiResponse<List<OfferResponse>> apiResponse = new ApiResponse<>(
                    true,
                    "Offers retrieved successfully",
                    offers
            );

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            logger.error("Error fetching offers by status: " + status, e);
            ApiResponse<List<OfferResponse>> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to fetch offers: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<OfferResponse>>> searchOffers(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) OfferStatus status,
            Authentication authentication) {
        try {
            logger.info("Searching offers with term: {} and status: {}", searchTerm, status);

            String adminEmail = authentication.getName();
            List<OfferResponse> offers = offerService.searchOffers(searchTerm, status, adminEmail);

            ApiResponse<List<OfferResponse>> apiResponse = new ApiResponse<>(
                    true,
                    "Search completed successfully",
                    offers
            );

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            logger.error("Error searching offers", e);
            ApiResponse<List<OfferResponse>> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to search offers: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<OfferResponse>>> getOffersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        try {
            logger.info("Fetching offers between {} and {}", startDate, endDate);

            String adminEmail = authentication.getName();
            List<OfferResponse> offers = offerService.getOffersByDateRange(startDate, endDate, adminEmail);

            ApiResponse<List<OfferResponse>> apiResponse = new ApiResponse<>(
                    true,
                    "Offers retrieved successfully",
                    offers
            );

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            logger.error("Error fetching offers by date range", e);
            ApiResponse<List<OfferResponse>> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to fetch offers: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OfferResponse>> updateOfferStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> statusUpdate,
            Authentication authentication) {
        try {
            logger.info("Updating offer {} status", id);

            String statusString = statusUpdate.get("status");
            if (statusString == null) {
                throw new IllegalArgumentException("Status is required");
            }

            OfferStatus status = OfferStatus.valueOf(statusString.toUpperCase());
            String adminEmail = authentication.getName();

            OfferResponse response = offerService.updateOfferStatus(id, status, adminEmail);

            ApiResponse<OfferResponse> apiResponse = new ApiResponse<>(
                    true,
                    "Offer status updated successfully",
                    response
            );

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            logger.error("Error updating offer status: " + id, e);
            ApiResponse<OfferResponse> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to update offer status: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadOffer(
            @PathVariable String id,
            Authentication authentication) {
        try {
            logger.info("Downloading offer PDF: {}", id);

            String adminEmail = authentication.getName();
            byte[] pdfBytes = offerService.downloadOffer(id, adminEmail);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "offer_letter_" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            logger.error("Error downloading offer PDF: " + id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOffer(
            @PathVariable String id,
            Authentication authentication) {
        try {
            logger.info("Deleting offer: {}", id);

            String adminEmail = authentication.getName();
            offerService.deleteOffer(id, adminEmail);

            ApiResponse<String> apiResponse = new ApiResponse<>(
                    true,
                    "Offer deleted successfully",
                    "Offer with ID " + id + " has been deleted"
            );

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            logger.error("Error deleting offer: " + id, e);
            ApiResponse<String> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to delete offer: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        ApiResponse<String> response = new ApiResponse<>(
                true,
                "Offer service is running",
                "OK"
        );
        return ResponseEntity.ok(response);
    }

    // Get available offer statuses
    @GetMapping("/statuses")
    public ResponseEntity<ApiResponse<OfferStatus[]>> getAvailableStatuses() {
        try {
            ApiResponse<OfferStatus[]> apiResponse = new ApiResponse<>(
                    true,
                    "Available statuses retrieved successfully",
                    OfferStatus.values()
            );

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            logger.error("Error fetching available statuses", e);
            ApiResponse<OfferStatus[]> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to fetch statuses: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}