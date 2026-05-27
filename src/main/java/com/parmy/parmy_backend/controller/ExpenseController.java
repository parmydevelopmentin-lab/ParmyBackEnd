package com.parmy.parmy_backend.controller;

import java.time.LocalDate;
import java.util.List;

import com.parmy.parmy_backend.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.parmy.parmy_backend.dto.ExpenseAnalyticsResponse;
import com.parmy.parmy_backend.dto.ExpenseRequest;
import com.parmy.parmy_backend.dto.ExpenseResponse;
import com.parmy.parmy_backend.model.ExpenseCategory;
import com.parmy.parmy_backend.service.ExpensePDFService;
import com.parmy.parmy_backend.service.ExpenseService;
import com.parmy.parmy_backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = {"https://parmytechnologies.netlify.app", "http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpensePDFService expensePDFService;

    @Autowired
    private JwtUtil jwtUtil;

    // Create new expense (Admin only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @Valid @RequestBody ExpenseRequest request,
            HttpServletRequest httpRequest) {
        try {
            String token = extractToken(httpRequest);
            String userEmail = jwtUtil.extractEmail(token);
            
            ApiResponse<ExpenseResponse> response = expenseService.createExpense(request, userEmail);
            return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                    .body(response);
        } catch (Exception e) {
            ApiResponse<ExpenseResponse> errorResponse = new ApiResponse<>(false, "Failed to create expense: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get all expenses (Admin only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getAllExpenses() {
        try {
            ApiResponse<List<ExpenseResponse>> response = expenseService.getAllExpenses();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<ExpenseResponse>> errorResponse = new ApiResponse<>(false, "Failed to retrieve expenses: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get expense by ID (Admin only)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(@PathVariable String id) {
        try {
            ApiResponse<ExpenseResponse> response = expenseService.getExpenseById(id);
            return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                    .body(response);
        } catch (Exception e) {
            ApiResponse<ExpenseResponse> errorResponse = new ApiResponse<>(false, "Failed to retrieve expense: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Update expense (Admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable String id,
            @Valid @RequestBody ExpenseRequest request) {
        try {
            ApiResponse<ExpenseResponse> response = expenseService.updateExpense(id, request);
            return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                    .body(response);
        } catch (Exception e) {
            ApiResponse<ExpenseResponse> errorResponse = new ApiResponse<>(false, "Failed to update expense: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Delete expense (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteExpense(@PathVariable String id) {
        try {
            ApiResponse<String> response = expenseService.deleteExpense(id);
            return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                    .body(response);
        } catch (Exception e) {
            ApiResponse<String> errorResponse = new ApiResponse<>(false, "Failed to delete expense: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Filter expenses by category (Admin only)
    @GetMapping("/filter/category/{category}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpensesByCategory(@PathVariable String category) {
        try {
            ExpenseCategory expenseCategory = ExpenseCategory.valueOf(category.toUpperCase());
            ApiResponse<List<ExpenseResponse>> response = expenseService.getExpensesByCategory(expenseCategory);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<List<ExpenseResponse>> errorResponse = new ApiResponse<>(false, "Invalid category: " + category, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ApiResponse<List<ExpenseResponse>> errorResponse = new ApiResponse<>(false, "Failed to filter expenses: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Filter expenses by date range (Admin only)
    @GetMapping("/filter/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpensesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            ApiResponse<List<ExpenseResponse>> response = expenseService.getExpensesByDateRange(startDate, endDate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<ExpenseResponse>> errorResponse = new ApiResponse<>(false, "Failed to filter expenses by date range: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Filter expenses by month (Admin only)
    @GetMapping("/filter/month/{year}/{month}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpensesByMonth(
            @PathVariable int year,
            @PathVariable int month) {
        try {
            ApiResponse<List<ExpenseResponse>> response = expenseService.getExpensesByMonth(year, month);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<ExpenseResponse>> errorResponse = new ApiResponse<>(false, "Failed to filter expenses by month: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Search expenses by title (Admin only)
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> searchExpensesByTitle(@RequestParam String title) {
        try {
            ApiResponse<List<ExpenseResponse>> response = expenseService.searchExpensesByTitle(title);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<ExpenseResponse>> errorResponse = new ApiResponse<>(false, "Failed to search expenses: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get expense analytics (Admin only)
    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ExpenseAnalyticsResponse>> getExpenseAnalytics() {
        try {
            ApiResponse<ExpenseAnalyticsResponse> response = expenseService.getExpenseAnalytics();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<ExpenseAnalyticsResponse> errorResponse = new ApiResponse<>(false, "Failed to retrieve analytics: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get expense analytics for date range (Admin only)
    @GetMapping("/analytics/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ExpenseAnalyticsResponse>> getExpenseAnalyticsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            ApiResponse<ExpenseAnalyticsResponse> response = expenseService.getExpenseAnalyticsByDateRange(startDate, endDate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<ExpenseAnalyticsResponse> errorResponse = new ApiResponse<>(false, "Failed to retrieve analytics: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Export expenses to PDF (Admin only)
    @GetMapping("/export/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportExpensesToPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            ApiResponse<List<ExpenseResponse>> expensesResponse;
            ApiResponse<ExpenseAnalyticsResponse> analyticsResponse;

            // Get expenses and analytics based on date range
            if (startDate != null && endDate != null) {
                expensesResponse = expenseService.getExpensesByDateRange(startDate, endDate);
                analyticsResponse = expenseService.getExpenseAnalyticsByDateRange(startDate, endDate);
            } else {
                expensesResponse = expenseService.getAllExpenses();
                analyticsResponse = expenseService.getExpenseAnalytics();
            }

            if (!expensesResponse.isSuccess() || !analyticsResponse.isSuccess()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // Convert ExpenseResponse to Expense for PDF generation
            List<ExpenseResponse> expenseResponses = expensesResponse.getData();
            List<Expense> expenses = expenseResponses.stream()
                    .map(this::convertToExpense)
                    .toList();

            byte[] pdfBytes = expensePDFService.generateExpenseReportPdf(expenses, analyticsResponse.getData());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "expense_report_" + LocalDate.now() + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get available categories (Admin only)
    @GetMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableCategories() {
        try {
            ApiResponse<List<String>> response = expenseService.getAvailableCategories();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<String>> errorResponse = new ApiResponse<>(false, "Failed to retrieve categories: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Helper method to extract JWT token from request
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Helper method to convert ExpenseResponse to Expense for PDF generation
    private Expense convertToExpense(ExpenseResponse response) {
        Expense expense = new Expense();
        expense.setId(response.getId());
        expense.setTitle(response.getTitle());
        expense.setCategory(response.getCategory());
        expense.setAmount(response.getAmount());
        expense.setExpenseDate(response.getExpenseDate());
        expense.setNotes(response.getNotes());
        expense.setCreatedBy(response.getCreatedBy());
        expense.setCreatedAt(response.getCreatedAt());
        expense.setUpdatedAt(response.getUpdatedAt());
        return expense;
    }
}