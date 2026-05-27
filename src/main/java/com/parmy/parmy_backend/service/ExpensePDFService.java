package com.parmy.parmy_backend.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.parmy.parmy_backend.dto.ExpenseAnalyticsResponse;
import com.parmy.parmy_backend.model.Expense;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class ExpensePDFService {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);

    public byte[] generateExpenseReportPdf(List<Expense> expenses, ExpenseAnalyticsResponse analytics)
            throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);

        document.open();

        try {
            // Add company header
            addCompanyHeader(document);

            // Add report title and date
            addReportHeader(document);

            // Add analytics summary
            addAnalyticsSummary(document, analytics);

            // Add category breakdown
            addCategoryBreakdown(document, analytics);

            // Add detailed expense table
            addExpenseTable(document, expenses);

            // Add footer
            addFooter(document);

        } catch (Exception e) {
            throw new DocumentException("Error generating PDF: " + e.getMessage());
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }

    private void addCompanyHeader(Document document) throws DocumentException {
        // Company name and logo section
        Paragraph companyName = new Paragraph("PARMY TECHNOLOGIES", TITLE_FONT);
        companyName.setAlignment(Element.ALIGN_CENTER);
        companyName.setSpacingAfter(5f);
        document.add(companyName);

        Paragraph tagline = new Paragraph("Expense Management Report",
                new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.GRAY));
        tagline.setAlignment(Element.ALIGN_CENTER);
        tagline.setSpacingAfter(20f);
        document.add(tagline);

        // Add a line separator
        Paragraph separator = new Paragraph("_________________________________________________________________");
        separator.setAlignment(Element.ALIGN_CENTER);
        separator.setSpacingAfter(20f);
        document.add(separator);
    }

    private void addReportHeader(Document document) throws DocumentException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

        Paragraph reportTitle = new Paragraph("Company Expense Report", TITLE_FONT);
        reportTitle.setAlignment(Element.ALIGN_LEFT);
        reportTitle.setSpacingAfter(10f);
        document.add(reportTitle);

        Paragraph reportDate = new Paragraph("Generated on: " + LocalDate.now().format(formatter), NORMAL_FONT);
        reportDate.setAlignment(Element.ALIGN_LEFT);
        reportDate.setSpacingAfter(20f);
        document.add(reportDate);
    }

    private void addAnalyticsSummary(Document document, ExpenseAnalyticsResponse analytics) throws DocumentException {
        Paragraph summaryTitle = new Paragraph("EXECUTIVE SUMMARY", BOLD_FONT);
        summaryTitle.setSpacingAfter(10f);
        document.add(summaryTitle);

        // Create summary table
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingAfter(20f);

        // Add summary data
        addSummaryRow(summaryTable, "Total Expenses:", String.format("₹%.2f", analytics.getTotalExpenses()));
        addSummaryRow(summaryTable, "Total Transactions:", String.valueOf(analytics.getTotalTransactions()));

        if (analytics.getCurrentMonth() != null) {
            addSummaryRow(summaryTable, "Current Month Total:",
                    String.format("₹%.2f", analytics.getCurrentMonth().getTotal()));
            addSummaryRow(summaryTable, "Current Month Transactions:",
                    String.valueOf(analytics.getCurrentMonth().getCount()));
        }

        if (analytics.getLastMonth() != null) {
            addSummaryRow(summaryTable, "Previous Month Total:",
                    String.format("₹%.2f", analytics.getLastMonth().getTotal()));
        }

        document.add(summaryTable);
    }

    private void addSummaryRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5f);
        table.addCell(valueCell);
    }

    private void addCategoryBreakdown(Document document, ExpenseAnalyticsResponse analytics) throws DocumentException {
        if (analytics.getCategoryBreakdown() == null || analytics.getCategoryBreakdown().isEmpty()) {
            return;
        }

        Paragraph categoryTitle = new Paragraph("CATEGORY BREAKDOWN", BOLD_FONT);
        categoryTitle.setSpacingAfter(10f);
        document.add(categoryTitle);

        // Create category table
        PdfPTable categoryTable = new PdfPTable(3);
        categoryTable.setWidthPercentage(100);
        categoryTable.setWidths(new float[] { 3f, 2f, 2f });
        categoryTable.setSpacingAfter(20f);

        // Add headers
        addHeaderCell(categoryTable, "Category");
        addHeaderCell(categoryTable, "Amount");
        addHeaderCell(categoryTable, "Percentage");

        // Calculate total for percentage
        double total = analytics.getTotalExpenses();

        // Add category data
        for (Map.Entry<String, Double> entry : analytics.getCategoryBreakdown().entrySet()) {
            addDataCell(categoryTable, entry.getKey());
            addDataCell(categoryTable, String.format("₹%.2f", entry.getValue()));
            double percentage = total > 0 ? (entry.getValue() / total) * 100 : 0;
            addDataCell(categoryTable, String.format("%.1f%%", percentage));
        }

        document.add(categoryTable);
    }

    private void addExpenseTable(Document document, List<Expense> expenses) throws DocumentException {
        Paragraph tableTitle = new Paragraph("DETAILED EXPENSE TRANSACTIONS", BOLD_FONT);
        tableTitle.setSpacingAfter(10f);
        document.add(tableTitle);

        // Create main table
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 3f, 2f, 2f, 1.5f, 2.5f });

        // Add headers
        addHeaderCell(table, "Title");
        addHeaderCell(table, "Category");
        addHeaderCell(table, "Amount");
        addHeaderCell(table, "Date");
        addHeaderCell(table, "Notes");

        // Add expense data
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        for (Expense expense : expenses) {
            addDataCell(table, expense.getTitle());
            addDataCell(table, expense.getCategory().getDisplayName());
            addDataCell(table, String.format("₹%.2f", expense.getAmount()));
            addDataCell(table, expense.getExpenseDate().format(dateFormatter));
            addDataCell(table, expense.getNotes() != null ? expense.getNotes() : "");
        }

        document.add(table);
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(BaseColor.DARK_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8f);
        table.addCell(cell);
    }

    private void addDataCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL_FONT));
        cell.setPadding(5f);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void addFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph(
                "\n\nThis report was generated automatically by PARMY TECHNOLOGIES Expense Management System.",
                new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20f);
        document.add(footer);

        Paragraph disclaimer = new Paragraph("For internal use only. All financial data is confidential.",
                new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY));
        disclaimer.setAlignment(Element.ALIGN_CENTER);
        document.add(disclaimer);
    }

    // Generate simplified expense list PDF
    public byte[] generateExpenseListPdf(List<Expense> expenses) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);

        document.open();

        try {
            // Add simple header
            Paragraph title = new Paragraph("Expense List", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Add generation date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            Paragraph date = new Paragraph("Generated on: " + LocalDate.now().format(formatter), NORMAL_FONT);
            date.setAlignment(Element.ALIGN_CENTER);
            date.setSpacingAfter(20f);
            document.add(date);

            // Add expense table
            addExpenseTable(document, expenses);

            // Add total
            double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
            Paragraph totalParagraph = new Paragraph(String.format("\nTotal Amount: ₹%.2f", total), BOLD_FONT);
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalParagraph);

        } catch (Exception e) {
            throw new DocumentException("Error generating simple PDF: " + e.getMessage());
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }
}