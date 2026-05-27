package com.parmy.parmy_backend.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.parmy.parmy_backend.model.Invoice;
import com.parmy.parmy_backend.model.InvoiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PDFService {
    
    private static final Logger logger = LoggerFactory.getLogger(PDFService.class);
    
    // Colors
    private static final BaseColor PRIMARY_COLOR = new BaseColor(20, 32, 44); // Dark blue
    private static final BaseColor SECONDARY_COLOR = new BaseColor(52, 168, 83); // Green
    private static final BaseColor GRAY_COLOR = new BaseColor(107, 114, 128);
    private static final BaseColor LIGHT_GRAY = new BaseColor(243, 244, 246);
    
    // Fonts
    private Font titleFont;
    private Font headerFont;
    private Font normalFont;
    private Font boldFont;
    private Font smallFont;
    
    public PDFService() {
        initializeFonts();
    }
    
    private void initializeFonts() {
        try {
            titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, PRIMARY_COLOR);
            headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, PRIMARY_COLOR);
            normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
            boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);
            smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, GRAY_COLOR);
        } catch (Exception e) {
            logger.error("Error initializing fonts", e);
            // Fallback to default fonts
            titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, PRIMARY_COLOR);
            headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR);
            normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
            boldFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);
            smallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, GRAY_COLOR);
        }
    }
    
    /**
     * Generate PDF invoice as byte array
     * @param invoice the invoice to generate PDF for
     * @return PDF as byte array
     * @throws DocumentException if PDF generation fails
     */
    public byte[] generateInvoicePDF(Invoice invoice) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();
            
            // Add watermark if paid
            if (invoice.getStatus() == InvoiceStatus.PAID) {
                addWatermark(writer, "PAID");
            }
            
            // Header with company info
            addHeader(document);
            
            // Invoice details
            addInvoiceDetails(document, invoice);
            
            // Customer information
            addCustomerInfo(document, invoice);
            
            // Invoice items table
            addInvoiceTable(document, invoice);
            
            // Payment terms and conditions
            addTermsAndConditions(document);
            
            // Footer
            addFooter(document, invoice);
            
            document.close();
            logger.info("PDF generated successfully for invoice: {}", invoice.getInvoiceNumber());
            
        } catch (Exception e) {
            logger.error("Error generating PDF for invoice: {}", invoice.getInvoiceNumber(), e);
            throw e;
        }
        
        return baos.toByteArray();
    }
    
    /**
     * Save PDF to file system
     * @param invoice the invoice
     * @param filePath the file path to save
     * @throws DocumentException if PDF generation fails
     * @throws IOException if file save fails
     */
    public void savePDFToFile(Invoice invoice, String filePath) throws DocumentException, IOException {
        byte[] pdfBytes = generateInvoicePDF(invoice);
        
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(pdfBytes);
            logger.info("PDF saved to file: {}", filePath);
        }
    }
    
    private void addWatermark(PdfWriter writer, String text) {
        try {
            PdfContentByte canvas = writer.getDirectContentUnder();
            Phrase watermark = new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 80, BaseColor.LIGHT_GRAY));
            
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 
                                       PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2, 45);
        } catch (Exception e) {
            logger.warn("Failed to add watermark", e);
        }
    }
    
    private void addHeader(Document document) throws DocumentException {
        // Company Header
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{3, 2});
        
        // Company Info
        PdfPCell companyCell = new PdfPCell();
        companyCell.setBorder(Rectangle.NO_BORDER);
        companyCell.setPadding(10);
        
        Paragraph companyName = new Paragraph("PARMY TECHNOLOGIES PVT LTD", titleFont);
        companyName.setSpacingAfter(5);
        companyCell.addElement(companyName);
        
        Paragraph tagline = new Paragraph("Transforming Ideas into Digital Reality", 
                                         FontFactory.getFont(FontFactory.HELVETICA, 12, SECONDARY_COLOR));
        tagline.setSpacingAfter(10);
        companyCell.addElement(tagline);
        
        companyCell.addElement(new Paragraph("Plot 77, Road No. 7, Kolan Shiva Reddy Nagar", normalFont));
        companyCell.addElement(new Paragraph("Hyderabad, Telangana 501505, India", normalFont));
        companyCell.addElement(new Paragraph("Phone: +91 7670968622", normalFont));
        companyCell.addElement(new Paragraph("Email: info@parmy.com", normalFont));
        companyCell.addElement(new Paragraph("Website: https://parmytechnologies.com", normalFont));
        
        headerTable.addCell(companyCell);
        
        // Invoice Title
        PdfPCell invoiceCell = new PdfPCell();
        invoiceCell.setBorder(Rectangle.NO_BORDER);
        invoiceCell.setPadding(10);
        invoiceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        Paragraph invoiceTitle = new Paragraph("INVOICE", 
                                              FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, PRIMARY_COLOR));
        invoiceTitle.setAlignment(Element.ALIGN_RIGHT);
        invoiceCell.addElement(invoiceTitle);
        
        headerTable.addCell(invoiceCell);
        
        document.add(headerTable);
        document.add(new Paragraph(" ", normalFont)); // Spacing
    }
    
    private void addInvoiceDetails(Document document, Invoice invoice) throws DocumentException {
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setWidths(new float[]{1, 1});
        detailsTable.setSpacingBefore(10);
        detailsTable.setSpacingAfter(10);
        
        // Invoice Number and Date
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPadding(10);
        
        leftCell.addElement(new Paragraph("Invoice Number: " + invoice.getInvoiceNumber(), boldFont));
        leftCell.addElement(new Paragraph("Date: " + invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")), normalFont));
        leftCell.addElement(new Paragraph("Due Date: " + invoice.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")), normalFont));
        
        detailsTable.addCell(leftCell);
        
        // Status
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setPadding(10);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        BaseColor statusColor = getStatusColor(invoice.getStatus());
        Font statusFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, statusColor);
        
        Paragraph status = new Paragraph("Status: " + invoice.getStatus().getDisplayName(), statusFont);
        status.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(status);
        
        if (invoice.isOverdue()) {
            Paragraph overdue = new Paragraph("OVERDUE", 
                                             FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.RED));
            overdue.setAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(overdue);
        }
        
        detailsTable.addCell(rightCell);
        
        document.add(detailsTable);
    }
    
    private void addCustomerInfo(Document document, Invoice invoice) throws DocumentException {
        // Bill To section
        Paragraph billToTitle = new Paragraph("BILL TO:", headerFont);
        billToTitle.setSpacingBefore(20);
        billToTitle.setSpacingAfter(5);
        document.add(billToTitle);
        
        PdfPTable customerTable = new PdfPTable(1);
        customerTable.setWidthPercentage(50);
        customerTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        
        PdfPCell customerCell = new PdfPCell();
        customerCell.setBorder(Rectangle.BOX);
        customerCell.setBorderColor(LIGHT_GRAY);
        customerCell.setPadding(15);
        customerCell.setBackgroundColor(new BaseColor(249, 250, 251));
        
        customerCell.addElement(new Paragraph(invoice.getCustomerName(), boldFont));
        customerCell.addElement(new Paragraph(invoice.getCustomerEmail(), normalFont));
        
        customerTable.addCell(customerCell);
        document.add(customerTable);
        
        document.add(new Paragraph(" ", normalFont)); // Spacing
    }
    
    private void addInvoiceTable(Document document, Invoice invoice) throws DocumentException {
        // Table header
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 1, 2, 2, 2});
        table.setSpacingBefore(20);
        
        // Header cells
        String[] headers = {"Description", "Qty", "Unit Price", "Discount", "Amount"};
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, 
                                              FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE)));
            headerCell.setBackgroundColor(PRIMARY_COLOR);
            headerCell.setPadding(10);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(headerCell);
        }
        
        // Data row
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        
        // Description
        PdfPCell descCell = new PdfPCell(new Phrase(invoice.getProjectName(), normalFont));
        descCell.setPadding(10);
        table.addCell(descCell);
        
        // Quantity
        PdfPCell qtyCell = new PdfPCell(new Phrase(invoice.getQuantity().toString(), normalFont));
        qtyCell.setPadding(10);
        qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(qtyCell);
        
        // Unit Price
        PdfPCell priceCell = new PdfPCell(new Phrase(currencyFormat.format(invoice.getPrice()), normalFont));
        priceCell.setPadding(10);
        priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(priceCell);
        
        // Discount
        String discountText = invoice.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0 
                             ? invoice.getDiscountPercentage() + "%" 
                             : "-";
        PdfPCell discountCell = new PdfPCell(new Phrase(discountText, normalFont));
        discountCell.setPadding(10);
        discountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(discountCell);
        
        // Amount (subtotal)
        PdfPCell amountCell = new PdfPCell(new Phrase(currencyFormat.format(invoice.getSubtotal()), normalFont));
        amountCell.setPadding(10);
        amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(amountCell);
        
        document.add(table);
        
        // Totals section
        addTotalsSection(document, invoice);
    }
    
    private void addTotalsSection(Document document, Invoice invoice) throws DocumentException {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(50);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalsTable.setSpacingBefore(20);
        
        // Subtotal
        totalsTable.addCell(createTotalCell("Subtotal:", normalFont, Element.ALIGN_RIGHT));
        totalsTable.addCell(createTotalCell(currencyFormat.format(invoice.getSubtotal()), normalFont, Element.ALIGN_RIGHT));
        
        // Discount
        if (invoice.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            totalsTable.addCell(createTotalCell("Discount (" + invoice.getDiscountPercentage() + "%):", normalFont, Element.ALIGN_RIGHT));
            totalsTable.addCell(createTotalCell("-" + currencyFormat.format(invoice.getDiscountAmount()), normalFont, Element.ALIGN_RIGHT));
        }
        
        // Tax
        if (invoice.getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
            totalsTable.addCell(createTotalCell("Tax (" + invoice.getTaxPercentage() + "%):", normalFont, Element.ALIGN_RIGHT));
            totalsTable.addCell(createTotalCell(currencyFormat.format(invoice.getTaxAmount()), normalFont, Element.ALIGN_RIGHT));
        }
        
        // Total
        PdfPCell totalLabelCell = createTotalCell("TOTAL:", boldFont, Element.ALIGN_RIGHT);
        totalLabelCell.setBackgroundColor(LIGHT_GRAY);
        totalLabelCell.setBorderWidth(2);
        totalLabelCell.setBorderColor(PRIMARY_COLOR);
        
        PdfPCell totalAmountCell = createTotalCell(currencyFormat.format(invoice.getTotalAmount()), 
                                                  FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, PRIMARY_COLOR), 
                                                  Element.ALIGN_RIGHT);
        totalAmountCell.setBackgroundColor(LIGHT_GRAY);
        totalAmountCell.setBorderWidth(2);
        totalAmountCell.setBorderColor(PRIMARY_COLOR);
        
        totalsTable.addCell(totalLabelCell);
        totalsTable.addCell(totalAmountCell);
        
        document.add(totalsTable);
    }
    
    private PdfPCell createTotalCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }
    
    private void addTermsAndConditions(Document document) throws DocumentException {
        Paragraph termsTitle = new Paragraph("TERMS & CONDITIONS", headerFont);
        termsTitle.setSpacingBefore(30);
        termsTitle.setSpacingAfter(10);
        document.add(termsTitle);
        
        String[] terms = {
            "Payment is due within 30 days of invoice date.",
            "Late payments may be subject to 1.5% monthly service charge.",
            "All work is performed according to our standard terms of service.",
            "Any disputes must be resolved within 30 days of invoice date.",
            "This invoice is generated electronically and is valid without signature."
        };
        
        for (String term : terms) {
            Paragraph termParagraph = new Paragraph("• " + term, smallFont);
            termParagraph.setSpacingAfter(3);
            document.add(termParagraph);
        }
    }
    
    private void addFooter(Document document, Invoice invoice) throws DocumentException {
        Paragraph footer = new Paragraph("Thank you for your business!", 
                                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, SECONDARY_COLOR));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        footer.setSpacingAfter(10);
        document.add(footer);
        
        Paragraph contact = new Paragraph("For questions about this invoice, contact us at https://parmytechnologies.com or +91 7670968622",
                                         smallFont);
        contact.setAlignment(Element.ALIGN_CENTER);
        document.add(contact);
    }
    
    private BaseColor getStatusColor(InvoiceStatus status) {
        switch (status) {
            case PAID:
                return SECONDARY_COLOR; // Green
            case UNPAID:
            case OVERDUE:
                return BaseColor.RED;
            case PENDING:
                return new BaseColor(251, 191, 36); // Yellow
            case CANCELLED:
                return GRAY_COLOR;
            default:
                return BaseColor.BLACK;
        }
    }
}