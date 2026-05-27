package com.parmy.parmy_backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.parmy.parmy_backend.model.Offer;

@Service
public class OfferLetterPDFService {
    
    private static final Logger logger = LoggerFactory.getLogger(OfferLetterPDFService.class);
    
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
    private static final Font SMALL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
    
    public byte[] generateOfferLetterPDF(Offer offer) throws DocumentException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            // Load the template PDF
            ClassPathResource templateResource = new ClassPathResource("templates/letterhead.pdf");
            InputStream templateStream = templateResource.getInputStream();
            PdfReader templateReader = new PdfReader(templateStream);
            
            // Create a stamper to overlay content on template
            PdfStamper stamper = new PdfStamper(templateReader, baos);
            
            // Add content to the template's first page
            addFirstPageContent(stamper.getOverContent(1), offer);
            
            // Add second page with letterhead template
            stamper.insertPage(2, PageSize.A4);
            copyLetterheadToPage(stamper, templateReader, 2);
            addSecondPageContent(stamper.getOverContent(2), offer);
            
            // Add third page with letterhead template
            stamper.insertPage(3, PageSize.A4);
            copyLetterheadToPage(stamper, templateReader, 3);
            addThirdPageContent(stamper.getOverContent(3), offer);
            
            // Add fourth page with letterhead template for signature section
            stamper.insertPage(4, PageSize.A4);
            copyLetterheadToPage(stamper, templateReader, 4);
            addFourthPageContent(stamper.getOverContent(4), offer);
            
            stamper.close();
            templateReader.close();
            templateStream.close();
            
        } catch (DocumentException | IOException e) {
            logger.error("Error generating PDF for offer: " + offer.getId(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
        
        return baos.toByteArray();
    }
    
    /**
     * Copy letterhead template from first page to a new page
     */
    private void copyLetterheadToPage(PdfStamper stamper, PdfReader templateReader, int pageNumber) throws DocumentException {
        try {
            // Get the template's first page as a template
            PdfContentByte under = stamper.getUnderContent(pageNumber);
            PdfTemplate template = stamper.getImportedPage(templateReader, 1);
            
            // Copy the letterhead template to the new page background
            under.addTemplate(template, 0, 0);
            
        } catch (Exception e) {
            logger.error("Error copying letterhead to page " + pageNumber, e);
            throw new DocumentException("Failed to copy letterhead to page " + pageNumber, e);
        }
    }
    
    private void addFirstPageContent(PdfContentByte contentByte, Offer offer) throws DocumentException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        
        try {
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            BaseFont boldFont = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            
            contentByte.beginText();
            
            // Date at top right corner
            contentByte.setFontAndSize(baseFont, 11);
            contentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, "Date: " + LocalDate.now().format(formatter), 545, 720, 0);
            
            contentByte.endText();
            
            // Main content using ColumnText - positioned below letterhead
            String recipientInfo = offer.getCandidateName() + "\n" +
                    (offer.getAddress() != null ? offer.getAddress() : "Hyderabad, India");
            
            ColumnText ct = new ColumnText(contentByte);
            ct.setSimpleColumn(50, 600, 545, 680);
            ct.addElement(new Paragraph(recipientInfo, NORMAL_FONT));
            ct.go();
            
            // Main letter content
            String mainContent = """
                Dear %s,
                
                Congratulations!
                
                                                                        Sub: Offer of Employment
                
                You have been selected to join Parmy Technologies Pvt.Ltd, a company that has been rated among the Top 5 best performing in Enterprise Mobility, Information management, and Cloud-based solutions in Hyderabad' Consistently for 5 years in a row. We are hopeful and confident that you will be able to build a successful career with us and become a part of the "out-performance" culture at Parmy Technologies Pvt. Ltd,
                
                We are pleased to offer you the position of %s in Parmy Technologies Pvt.Ltd commencing from the Date %s.
                
                At the time of joining, you are requested to bring copies of the following of our records:
                
                1. Certificates in support of your qualification (including degree certificates and final mark / grade sheets), experience and emoluments
                2. Relieving order / letter from your present employer (if fresher please ignore this)
                3. Salary certificate from your present employer (if fresher please ignore this)
                4. Four passport size photographs
                5. Address proof & Identity proof
                6. Relevant pages of your passport
                7. Resume / CV
                
                Please bring the original education certificates / mark sheets for verification.
                
                We look forward to you having a long and fruitful relationship with Parmy Technologies Pvt.Ltd
                """.formatted(offer.getCandidateName(), offer.getRole(), offer.getJoiningDate().format(formatter));
            
            ct = new ColumnText(contentByte);
            ct.setSimpleColumn(50, 200, 545, 590);
            ct.addElement(new Paragraph(mainContent, NORMAL_FONT));
            ct.go();
            
            // Signature section
            String signature = """
                [SIGNATURE]
                
                             
                For Parmy Technologies Pvt.Ltd,
                Sowjanya
                Senior Executive – HR
                """;
            
            ct = new ColumnText(contentByte);
            ct.setSimpleColumn(50, 50, 545, 200);
            ct.addElement(new Paragraph(signature, NORMAL_FONT));
            ct.go();
            ct.go();
            
        } catch (IOException e) {
            logger.error("Error adding first page content", e);
            throw new DocumentException("Failed to add first page content", e);
        }
    }
    
    private void addSecondPageContent(PdfContentByte contentByte, Offer offer) throws DocumentException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        
        try {
            // Second page content
            String secondPageContent = """
                
                TERMS AND CONDITIONS OF EMPLOYMENT
                
                1. APPOINTMENT
                
                a. Your date of appointment is effective from the date of joining which shall be as soon as possible but not later than %s.
                b. You will be on probation for a period of one year from the date of your appointment. If in the opinion of the Company you are found suitable in the appointed post you will be confirmed.
                
                2. COMPENSATION
                
                Your salary is personal and confidential information. Your employment shall be confirmed based on your performance, on successful completion of probation period of one year from the date of joining.
                
                3. LEAVE
                
                a. According to the Company's policy, your daily working hours will be according to client's requirement, with a 30 minute lunch break for all working days.                
                
                4. TRAINING
                
                If you are sent for training abroad, you will have to sign a bond for the period and the amount which will be decided by the Company as per its policy depending on the period of training, travel and other expenses / loss incurred.
                
                5. BACKGROUND VERIFICATION
                
                You have been employed on the basis of the particulars furnished by you. In case they said particulars are found to be incorrect or it is found that you have cancelled or withheld any relevant facts, your employment with the Company shall stand terminated / cancelled without any notice.
                
                6. TERMINATION OF EMPLOYMENT
                
                a. This contract would be deemed terminated by both the parties in the event of termination of work order by Parmy Technologies Pvt.Ltd
                b. In case, where we receive any update from the client stating that you shall be relieved from your assigned responsibilities due to project closure / otherwise, a notice period of not less than 15 days shall be avoided to you by Parmy Technologies Pvt.Ltd
                """.formatted(offer.getCandidateName(), offer.getRole(), offer.getJoiningDate().format(formatter));
            
            ColumnText ct = new ColumnText(contentByte);
            ct.setSimpleColumn(50, 100, 545, 750);
            ct.addElement(new Paragraph(secondPageContent, NORMAL_FONT));
            ct.go();
            
        } catch (Exception e) {
            logger.error("Error adding second page content", e);
            throw new DocumentException("Failed to add second page content", e);
        }
    }
    
    private void addThirdPageContent(PdfContentByte contentByte, Offer offer) throws DocumentException {
        try {
            // Third page content
            String thirdPageContent = """
                
                7. COMPANY POLICIES
                All Company policies (including access to office premises, mails, computer facilities, email and others) are available on the intranet. You are advised and instructed to go through these policies and strictly adhere to them.
                
                8. RULES & REGULATIONS
                
                You shall abide by the rules and regulations of the Company which are in the force from time to time. You may pleasure note that the Company reserves right to vary are modify any or all the rules and regulations from time to time if the deems fit in the interest of the Company. You shall comply with the rules and regulations as stipulated in the hand book of the Company. You shall also liable to execute confidentiality and non-disclosure agreements as per the requirement of the Company. You shall maintain discipline in the premises of the Company or the client and any violation thereof is liable to viewed seriously.
                
                9. INDEMNITY
                
                You shall agree to make good pay for any loss or damage that has been or might be incurred by Parmy Technologies Pvt.Ltd in respect of any acts / omissions of yours at the client's place the candidate which fosters liability, loss or damage claims on Parmy Technologies Pvt.Ltd
                
                10. PROTECTION OF INTEREST
                
                If you conceive any new or advanced methods of improving process / formulae / systems in relation to the operation of the Company, such development should be fully communicated to the Company and will be remain sole right / property of the Company. Also you will not undertake any parallel part time or full time employment with any of Parmy Technologies Pvt.Ltd competitor companies.
                
                11. NOTICE PERIOD
                
                This contract of employment is terminable, without reasons, by either party giving one month notice during probation period of on confirmation. Parmy Technologies Pvt.Ltd reserves the right to pay or recover salary in lieu of notice period. Further, the Company may at its discretion relieve you from such date as it may deem fit even prior to the expiry of the notice period.
                
                12. ON SEPARATION
                
                On acceptance of the separation notice, you will immediately give up to the Company before you are relieved, all correspondence, specifications, formulae, books, documents, cost of data, market data, literature, drawings, effects and shall not make or retain any copies of these items. Any other asset of the Company, furniture, vehicle, office equipment etc., will either be return to Company or retain on payment of such money as the Company may decide.
                """;
            
            ColumnText ct = new ColumnText(contentByte);
            ct.setSimpleColumn(50, 100, 545, 750);
            ct.addElement(new Paragraph(thirdPageContent, NORMAL_FONT));
            ct.go();
            
        } catch (Exception e) {
            logger.error("Error adding third page content", e);
            throw new DocumentException("Failed to add third page content", e);
        }
    }
    
    private void addFourthPageContent(PdfContentByte contentByte, Offer offer) throws DocumentException {
        try {
            // Signature and acceptance section
            String signatureSection = """
                             
                For Parmy Technologies Pvt.Ltd,
                     Sowjanya
                     Senior Executive – HR
                
                
                This is to certify that I have gone through and understood all the terms and conditions mentioned in this offer letter and I hereby accept and agree to abide by them:
                
                Full Name  : %s
                
                Signature   :
                
                Date    :
                """.formatted(offer.getCandidateName());
            
            ColumnText ct = new ColumnText(contentByte);
            ct.setSimpleColumn(50, 400, 545, 700);
            ct.addElement(new Paragraph(signatureSection, NORMAL_FONT));
            ct.go();
            
        } catch (Exception e) {
            logger.error("Error adding fourth page content", e);
            throw new DocumentException("Failed to add fourth page content", e);
        }
    }
}