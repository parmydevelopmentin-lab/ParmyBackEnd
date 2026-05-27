package com.parmy.parmy_backend.service;

import com.parmy.parmy_backend.dto.OfferRequest;
import com.parmy.parmy_backend.dto.OfferResponse;
import com.parmy.parmy_backend.model.Offer;
import com.parmy.parmy_backend.model.OfferStatus;
import com.parmy.parmy_backend.repository.OfferRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OfferService {
    
    private static final Logger logger = LoggerFactory.getLogger(OfferService.class);
    
    @Autowired
    private OfferRepository offerRepository;
    
    @Autowired
    private OfferLetterPDFService pdfService;
    
    @Autowired
    private JavaMailSender emailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    // Directory to store PDF files
    private final String PDF_STORAGE_DIR = "offer_letters/";
    
    public OfferResponse createOffer(OfferRequest request, String adminEmail) {
        try {
            logger.info("Creating new offer for candidate: {}", request.getCandidateEmail());
            
            // Check if candidate already has an active offer
            Optional<Offer> existingOffer = offerRepository.findActiveByCandidateEmail(request.getCandidateEmail());
            if (existingOffer.isPresent()) {
                throw new RuntimeException("Candidate already has an active offer. Please update or cancel the existing offer first.");
            }
            
            // Create new offer
            Offer offer = new Offer(
                request.getCandidateName(),
                request.getCandidateEmail(),
                request.getRole(),
                request.getJoiningDate(),
                request.getLocation(),
                request.getTrialPeriod(),
                request.getAddress(),
                adminEmail
            );
            
            // Generate PDF
            byte[] pdfBytes = pdfService.generateOfferLetterPDF(offer);
            
            // Save PDF to file system
            String fileName = generateFileName(offer);
            String filePath = savePDFToFile(pdfBytes, fileName);
            
            offer.setPdfFileName(fileName);
            offer.setPdfFilePath(filePath);
            
            // Save offer to database
            Offer savedOffer = offerRepository.save(offer);
            logger.info("Offer created successfully with ID: {}", savedOffer.getId());
            
            return convertToResponse(savedOffer);
            
        } catch (Exception e) {
            logger.error("Error creating offer for candidate: " + request.getCandidateEmail(), e);
            throw new RuntimeException("Failed to create offer: " + e.getMessage(), e);
        }
    }
    
    public OfferResponse sendOffer(String offerId, String adminEmail) {
        try {
            logger.info("Sending offer with ID: {}", offerId);
            
            Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found with ID: " + offerId));
            
            if (!offer.getCreatedBy().equals(adminEmail)) {
                throw new RuntimeException("You can only send offers created by you");
            }
            
            if (offer.isEmailSent()) {
                throw new RuntimeException("Offer has already been sent to the candidate");
            }
            
            // Read PDF from file system
            byte[] pdfBytes = readPDFFromFile(offer.getPdfFilePath());
            
            // Send email to candidate
            sendOfferEmail(offer, pdfBytes);
            
            // Send copy to admin
            sendAdminCopyEmail(offer, pdfBytes, adminEmail);
            
            // Update offer status
            offer.markAsSent();
            offer.setEmailSubject("Job Offer - " + offer.getRole() + " Position at PARMY TECHNOLOGIES PVT LTD");
            
            Offer updatedOffer = offerRepository.save(offer);
            logger.info("Offer sent successfully to: {}", offer.getCandidateEmail());
            
            return convertToResponse(updatedOffer);
            
        } catch (Exception e) {
            logger.error("Error sending offer with ID: " + offerId, e);
            throw new RuntimeException("Failed to send offer: " + e.getMessage(), e);
        }
    }
    
    public List<OfferResponse> getAllOffers(String adminEmail) {
        logger.info("Fetching all offers for admin: {}", adminEmail);
        List<Offer> offers = offerRepository.findAllByOrderByCreatedAtDesc();
        return offers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<OfferResponse> getOffersByStatus(OfferStatus status, String adminEmail) {
        logger.info("Fetching offers with status: {} for admin: {}", status, adminEmail);
        List<Offer> offers = offerRepository.findByStatus(status);
        return offers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<OfferResponse> searchOffers(String searchTerm, OfferStatus status, String adminEmail) {
        logger.info("Searching offers with term: {} and status: {} for admin: {}", searchTerm, status, adminEmail);
        List<Offer> offers = offerRepository.searchOffers(searchTerm, status);
        return offers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<OfferResponse> getOffersByDateRange(LocalDate startDate, LocalDate endDate, String adminEmail) {
        logger.info("Fetching offers between {} and {} for admin: {}", startDate, endDate, adminEmail);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        List<Offer> offers = offerRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        return offers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public OfferResponse updateOfferStatus(String offerId, OfferStatus status, String adminEmail) {
        try {
            logger.info("Updating offer {} status to: {}", offerId, status);
            
            Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found with ID: " + offerId));
            
            if (!offer.getCreatedBy().equals(adminEmail)) {
                throw new RuntimeException("You can only update offers created by you");
            }
            
            offer.setStatus(status);
            Offer updatedOffer = offerRepository.save(offer);
            
            logger.info("Offer status updated successfully");
            return convertToResponse(updatedOffer);
            
        } catch (Exception e) {
            logger.error("Error updating offer status: " + offerId, e);
            throw new RuntimeException("Failed to update offer status: " + e.getMessage(), e);
        }
    }
    
    public byte[] downloadOffer(String offerId, String adminEmail) {
        try {
            logger.info("Downloading offer PDF with ID: {}", offerId);
            
            Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found with ID: " + offerId));
            
            return readPDFFromFile(offer.getPdfFilePath());
            
        } catch (Exception e) {
            logger.error("Error downloading offer PDF: " + offerId, e);
            throw new RuntimeException("Failed to download offer PDF: " + e.getMessage(), e);
        }
    }
    
    public OfferResponse resendOffer(String offerId, String adminEmail) {
        try {
            logger.info("Resending offer with ID: {}", offerId);
            
            Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found with ID: " + offerId));
            
            if (!offer.getCreatedBy().equals(adminEmail)) {
                throw new RuntimeException("You can only resend offers created by you");
            }
            
            // Read PDF from file system
            byte[] pdfBytes = readPDFFromFile(offer.getPdfFilePath());
            
            // Send email to candidate
            sendOfferEmail(offer, pdfBytes);
            
            // Send copy to admin
            sendAdminCopyEmail(offer, pdfBytes, adminEmail);
            
            // Update timestamps
            offer.setEmailSent(true);
            offer.setEmailSentAt(LocalDateTime.now());
            offer.setUpdatedAt(LocalDateTime.now());
            
            Offer updatedOffer = offerRepository.save(offer);
            logger.info("Offer resent successfully to: {}", offer.getCandidateEmail());
            
            return convertToResponse(updatedOffer);
            
        } catch (Exception e) {
            logger.error("Error resending offer with ID: " + offerId, e);
            throw new RuntimeException("Failed to resend offer: " + e.getMessage(), e);
        }
    }
    
    public void deleteOffer(String offerId, String adminEmail) {
        try {
            logger.info("Deleting offer with ID: {}", offerId);
            
            Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found with ID: " + offerId));
            
            if (!offer.getCreatedBy().equals(adminEmail)) {
                throw new RuntimeException("You can only delete offers created by you");
            }
            
            // Delete PDF file
            deletePDFFile(offer.getPdfFilePath());
            
            // Delete from database
            offerRepository.delete(offer);
            
            logger.info("Offer deleted successfully");
            
        } catch (Exception e) {
            logger.error("Error deleting offer: " + offerId, e);
            throw new RuntimeException("Failed to delete offer: " + e.getMessage(), e);
        }
    }
    
    // Helper methods
    private String generateFileName(Offer offer) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String safeName = offer.getCandidateName().replaceAll("[^a-zA-Z0-9]", "_");
        return "offer_letter_" + safeName + "_" + timestamp + ".pdf";
    }
    
    private String savePDFToFile(byte[] pdfBytes, String fileName) throws IOException {
        // Create directory if it doesn't exist
        Path directory = Paths.get(PDF_STORAGE_DIR);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        
        // Save file
        Path filePath = directory.resolve(fileName);
        Files.write(filePath, pdfBytes);
        
        return filePath.toString();
    }
    
    private byte[] readPDFFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new RuntimeException("PDF file not found: " + filePath);
        }
        return Files.readAllBytes(path);
    }
    
    private void deletePDFFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                logger.info("PDF file deleted: {}", filePath);
            }
        } catch (IOException e) {
            logger.warn("Could not delete PDF file: " + filePath, e);
        }
    }
    
    private void sendOfferEmail(Offer offer, byte[] pdfBytes) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom(fromEmail);
        helper.setTo(offer.getCandidateEmail());
        helper.setSubject("Job Offer - " + offer.getRole() + " Position at PARMY TECHNOLOGIES PVT LTD");
        
        String emailBody = createOfferEmailBody(offer);
        helper.setText(emailBody, true);
        
        // Attach PDF
        helper.addAttachment(offer.getPdfFileName(), new ByteArrayResource(pdfBytes));
        
        emailSender.send(message);
        logger.info("Offer email sent to candidate: {}", offer.getCandidateEmail());
    }
    
    private void sendAdminCopyEmail(Offer offer, byte[] pdfBytes, String adminEmail) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom(fromEmail);
        helper.setTo(adminEmail);
        helper.setSubject("[COPY] Job Offer Sent - " + offer.getCandidateName() + " (" + offer.getRole() + ")");
        
        String emailBody = createAdminCopyEmailBody(offer);
        helper.setText(emailBody, true);
        
        // Attach PDF
        helper.addAttachment(offer.getPdfFileName(), new ByteArrayResource(pdfBytes));
        
        emailSender.send(message);
        logger.info("Admin copy email sent to: {}", adminEmail);
    }
    
    private String createOfferEmailBody(Offer offer) {
        return String.format("""
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .header { background-color: #f8f9fa; padding: 20px; border-left: 4px solid #007bff; }
                    .content { padding: 20px; }
                    .footer { background-color: #f8f9fa; padding: 15px; margin-top: 20px; }
                    .highlight { color: #007bff; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h2>🎉 Congratulations! Job Offer from PARMY TECHNOLOGIES PVT LTD</h2>
                </div>
                
                <div class="content">
                    <p>Dear <span class="highlight">%s</span>,</p>
                    
                    <p>We are excited to extend an offer for the position of <span class="highlight">%s</span> at PARMY TECHNOLOGIES PVT LTD.</p>
                    
                    <p><strong>Position Details:</strong></p>
                    <ul>
                        <li><strong>Role:</strong> %s</li>
                        <li><strong>Location:</strong> %s</li>
                        <li><strong>Joining Date:</strong> %s</li>
                        <li><strong>Trial Period:</strong> %s</li>
                    </ul>
                    
                    <p>Please find the detailed offer letter attached to this email. Kindly review all terms and conditions carefully.</p>
                    
                    <p>If you choose to accept this offer, please sign and return the offer letter by the deadline mentioned in the document.</p>
                    
                    <p>We look forward to welcoming you to the PARMY family! 🚀</p>
                    
                    <p>For any questions or clarifications, please don't hesitate to reach out to us.</p>
                </div>
                
                <div class="footer">
                    <p><strong>Best regards,</strong><br>
                    PARMY TECHNOLOGIES HR Team<br>
                    📧 %s<br>
                    🌐 https://parmytechnologies.com/</p>
                </div>
            </body>
            </html>
            """,
            offer.getCandidateName(),
            offer.getRole(),
            offer.getRole(),
            offer.getLocation(),
            offer.getJoiningDate().toString(),
            offer.getTrialPeriod(),
            fromEmail
        );
    }
    
    private String createAdminCopyEmailBody(Offer offer) {
        return String.format("""
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .header { background-color: #e7f3ff; padding: 20px; border-left: 4px solid #0066cc; }
                    .content { padding: 20px; }
                    .info-box { background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 15px 0; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h3>📋 Offer Letter Sent - Admin Copy</h3>
                </div>
                
                <div class="content">
                    <p><strong>Offer Letter Successfully Sent!</strong></p>
                    
                    <div class="info-box">
                        <p><strong>Candidate Details:</strong></p>
                        <ul>
                            <li><strong>Name:</strong> %s</li>
                            <li><strong>Email:</strong> %s</li>
                            <li><strong>Role:</strong> %s</li>
                            <li><strong>Location:</strong> %s</li>
                            <li><strong>Joining Date:</strong> %s</li>
                            <li><strong>Trial Period:</strong> %s</li>
                        </ul>
                    </div>
                    
                    <div class="info-box">
                        <p><strong>Email Details:</strong></p>
                        <ul>
                            <li><strong>Sent At:</strong> %s</li>
                            <li><strong>PDF Attached:</strong> Yes ✅</li>
                            <li><strong>Status:</strong> Delivered</li>
                        </ul>
                    </div>
                    
                    <p>The offer letter has been successfully delivered to the candidate. You can track the status and manage offers through the admin dashboard.</p>
                    
                    <p><em>This is an automated copy for your records.</em></p>
                </div>
            </body>
            </html>
            """,
            offer.getCandidateName(),
            offer.getCandidateEmail(),
            offer.getRole(),
            offer.getLocation(),
            offer.getJoiningDate().toString(),
            offer.getTrialPeriod(),
            LocalDateTime.now().toString()
        );
    }
    
    private OfferResponse convertToResponse(Offer offer) {
        return new OfferResponse(
            offer.getId(),
            offer.getCandidateName(),
            offer.getCandidateEmail(),
            offer.getRole(),
            offer.getJoiningDate(),
            offer.getLocation(),
            offer.getTrialPeriod(),
            offer.getAddress(),
            offer.getStatus(),
            offer.getCreatedBy(),
            offer.getCreatedAt(),
            offer.getUpdatedAt(),
            offer.getSentAt(),
            offer.isEmailSent(),
            offer.getEmailSentAt(),
            offer.getEmailSubject(),
            offer.getPdfFileName()
        );
    }
}