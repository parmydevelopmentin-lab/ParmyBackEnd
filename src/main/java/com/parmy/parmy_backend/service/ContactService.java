package com.parmy.parmy_backend.service;

import com.parmy.parmy_backend.dto.ContactRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ContactService {
    
    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String hostEmail;
    
    /**
     * Send contact form submission email to host
     * @param contactRequest the contact form data
     */
    public void sendContactFormEmail(ContactRequest contactRequest) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(hostEmail);
            message.setFrom(hostEmail);
            message.setSubject("New Contact Form Submission - " + contactRequest.getFullName());
            message.setText(buildContactFormEmailContent(contactRequest));
            message.setReplyTo(contactRequest.getEmail());
            
            mailSender.send(message);
            logger.info("Contact form email sent successfully to: {} from: {}", 
                       hostEmail, contactRequest.getEmail());
            
        } catch (Exception e) {
            logger.error("Failed to send contact form email from: {}", contactRequest.getEmail(), e);
            throw new RuntimeException("Failed to send contact form email", e);
        }
    }
    
    /**
     * Send acknowledgment email to the person who submitted the form
     * @param contactRequest the contact form data
     */
    public void sendAcknowledgmentEmail(ContactRequest contactRequest) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(contactRequest.getEmail());
            message.setFrom(hostEmail);
            message.setSubject("Thank you for contacting PARMY TECHNOLOGIES - We've received your message");
            message.setText(buildAcknowledgmentEmailContent(contactRequest));
            
            mailSender.send(message);
            logger.info("Acknowledgment email sent successfully to: {}", contactRequest.getEmail());
            
        } catch (Exception e) {
            logger.error("Failed to send acknowledgment email to: {}", contactRequest.getEmail(), e);
            // Don't throw exception here as the main contact email was already sent
            logger.warn("Contact form processed but acknowledgment email failed for: {}", 
                       contactRequest.getEmail());
        }
    }
    
    /**
     * Build the email content for host notification
     * @param contactRequest the contact form data
     * @return formatted email content
     */
    private String buildContactFormEmailContent(ContactRequest contactRequest) {
        StringBuilder content = new StringBuilder();
        
        content.append("🔔 NEW CONTACT FORM SUBMISSION\n");
        content.append("=" + "=".repeat(50) + "\n\n");
        
        // Submission Details
        content.append("📋 SUBMISSION DETAILS:\n");
        content.append("Date & Time: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"))).append("\n");
        content.append("Source: PARMY TECHNOLOGIES PVT LTD Website Contact Form\n\n");
        
        // Contact Information
        content.append("👤 CONTACT INFORMATION:\n");
        content.append("Name: ").append(contactRequest.getFullName()).append("\n");
        content.append("Email: ").append(contactRequest.getEmail()).append("\n");
        
        if (contactRequest.getPhone() != null && !contactRequest.getPhone().trim().isEmpty()) {
            content.append("Phone: ").append(contactRequest.getPhone()).append("\n");
        }
        
        if (contactRequest.getCompany() != null && !contactRequest.getCompany().trim().isEmpty()) {
            content.append("Company: ").append(contactRequest.getCompany()).append("\n");
        }
        
        content.append("\n");
        
        // Service & Location Information
        content.append("🎯 INQUIRY DETAILS:\n");
        
        if (contactRequest.getService() != null && !contactRequest.getService().trim().isEmpty()) {
            content.append("Service Interest: ").append(formatServiceName(contactRequest.getService())).append("\n");
        }
        
        if (contactRequest.getLocation() != null && !contactRequest.getLocation().trim().isEmpty()) {
            content.append("Preferred Location: ").append(formatLocationName(contactRequest.getLocation())).append("\n");
        }
        
        content.append("\n");
        
        // Message
        content.append("💬 MESSAGE:\n");
        content.append("-".repeat(30) + "\n");
        content.append(contactRequest.getMessage()).append("\n");
        content.append("-".repeat(30) + "\n\n");
        
        // Follow-up Instructions
        content.append("📝 NEXT STEPS:\n");
        content.append("• Reply to this email to respond directly to the client\n");
        content.append("• The client's email (").append(contactRequest.getEmail()).append(") is set as the reply-to address\n");
        content.append("• An acknowledgment email has been sent to the client\n");
        content.append("• Recommended response time: Within 24 hours\n\n");
        
        content.append("—\n");
        content.append("PARMY TECHNOLOGIES PVT LTD Contact Management System\n");
        content.append("This is an automated notification from your website contact form.");
        
        return content.toString();
    }
    
    /**
     * Build acknowledgment email content for the client
     * @param contactRequest the contact form data
     * @return formatted acknowledgment email
     */
    private String buildAcknowledgmentEmailContent(ContactRequest contactRequest) {
        StringBuilder content = new StringBuilder();
        
        content.append("Dear ").append(contactRequest.getFullName()).append(",\n\n");
        
        content.append("Thank you for reaching out to PARMY TECHNOLOGIES PVT LTD! We've successfully received your message and appreciate your interest in our services.\n\n");
        
        content.append("📋 Your submission details:\n");
        content.append("• Name: ").append(contactRequest.getFullName()).append("\n");
        content.append("• Email: ").append(contactRequest.getEmail()).append("\n");
        
        if (contactRequest.getService() != null && !contactRequest.getService().trim().isEmpty()) {
            content.append("• Service of Interest: ").append(formatServiceName(contactRequest.getService())).append("\n");
        }
        
        if (contactRequest.getLocation() != null && !contactRequest.getLocation().trim().isEmpty()) {
            content.append("• Preferred Location: ").append(formatLocationName(contactRequest.getLocation())).append("\n");
        }
        
        content.append("• Submitted: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"))).append("\n\n");
        
        content.append("🎯 What happens next?\n");
        content.append("Our team will review your inquiry and get back to you within 24 hours. Based on your requirements, we'll connect you with the right experts from our global team.\n\n");
        
        content.append("🌍 Our Locations:\n");
        content.append("• Hyderabad, India - AI & Machine Learning, Mobile Development, Cloud Services\n");

        content.append("If you have any urgent questions or need immediate assistance, please don't hesitate to contact us:\n");
        content.append("📧 Email: info@parmytechnologies.com\n");
        content.append("📱 Phone: +91 81252 45777 (India)\n");
        
        content.append("Best regards,\n");
        content.append("The PARMY TECHNOLOGIES Team\n\n");
        
        content.append("—\n");
        content.append("PARMY TECHNOLOGIES\n");
        content.append("Transforming Ideas into Digital Reality\n");
        content.append("🌐 Website: https://parmytechnologies.com\n");
        
        return content.toString();
    }
    
    /**
     * Format service names for display
     * @param service the service code
     * @return formatted service name
     */
    private String formatServiceName(String service) {
        switch (service.toLowerCase()) {
            case "software-development":
                return "Software Development";
            case "software-maintenance":
                return "Software Maintenance";
            case "seo":
                return "SEO Services";
            case "digital-marketing":
                return "Digital Marketing";
            case "ai-ml":
                return "AI & Machine Learning";
            case "cloud":
                return "Cloud Services";
            case "consultancy":
                return "Consultancy";
            case "other":
                return "Other Services";
            default:
                return service;
        }
    }
    
    /**
     * Format location names for display
     * @param location the location code
     * @return formatted location name
     */
    private String formatLocationName(String location) {
        switch (location.toLowerCase()) {
            case "hyderabad":
                return "Hyderabad, India";
            case "seattle":
                return "Seattle, USA";
            case "portland":
                return "Portland, USA";
            case "jeddah":
                return "Jeddah, Saudi Arabia";
            case "other":
                return "Other Location";
            default:
                return location;
        }
    }
}