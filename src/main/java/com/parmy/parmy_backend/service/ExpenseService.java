package com.parmy.parmy_backend.service;

import com.parmy.parmy_backend.dto.ApiResponse;
import com.parmy.parmy_backend.dto.ExpenseAnalyticsResponse;
import com.parmy.parmy_backend.dto.ExpenseRequest;
import com.parmy.parmy_backend.dto.ExpenseResponse;
import com.parmy.parmy_backend.model.Expense;
import com.parmy.parmy_backend.model.ExpenseCategory;
import com.parmy.parmy_backend.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    // Create a new expense
    public ApiResponse<ExpenseResponse> createExpense(ExpenseRequest request, String createdBy) {
        try {
            Expense expense = new Expense();
            expense.setTitle(request.getTitle());
            expense.setCategory(request.getCategory());
            expense.setAmount(request.getAmount());
            expense.setExpenseDate(request.getExpenseDate());
            expense.setNotes(request.getNotes());
            expense.setCreatedBy(createdBy);

            Expense savedExpense = expenseRepository.save(expense);
            ExpenseResponse response = new ExpenseResponse(savedExpense);

            return new ApiResponse<>(true, "Expense created successfully", response);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to create expense: " + e.getMessage(), null);
        }
    }

    // Get all expenses
    public ApiResponse<List<ExpenseResponse>> getAllExpenses() {
        try {
            List<Expense> expenses = expenseRepository.findAllByOrderByExpenseDateDesc();
            List<ExpenseResponse> responses = expenses.stream()
                    .map(ExpenseResponse::new)
                    .collect(Collectors.toList());

            return new ApiResponse<>(true, "Expenses retrieved successfully", responses);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve expenses: " + e.getMessage(), null);
        }
    }

    // Get expense by ID
    public ApiResponse<ExpenseResponse> getExpenseById(String id) {
        try {
            Optional<Expense> expenseOpt = expenseRepository.findById(id);
            if (expenseOpt.isPresent()) {
                ExpenseResponse response = new ExpenseResponse(expenseOpt.get());
                return new ApiResponse<>(true, "Expense retrieved successfully", response);
            } else {
                return new ApiResponse<>(false, "Expense not found", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve expense: " + e.getMessage(), null);
        }
    }

    // Update expense
    public ApiResponse<ExpenseResponse> updateExpense(String id, ExpenseRequest request) {
        try {
            Optional<Expense> expenseOpt = expenseRepository.findById(id);
            if (expenseOpt.isPresent()) {
                Expense expense = expenseOpt.get();
                expense.setTitle(request.getTitle());
                expense.setCategory(request.getCategory());
                expense.setAmount(request.getAmount());
                expense.setExpenseDate(request.getExpenseDate());
                expense.setNotes(request.getNotes());
                expense.updateTimestamp();

                Expense updatedExpense = expenseRepository.save(expense);
                ExpenseResponse response = new ExpenseResponse(updatedExpense);

                return new ApiResponse<>(true, "Expense updated successfully", response);
            } else {
                return new ApiResponse<>(false, "Expense not found", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to update expense: " + e.getMessage(), null);
        }
    }

    // Delete expense
    public ApiResponse<String> deleteExpense(String id) {
        try {
            if (expenseRepository.existsById(id)) {
                expenseRepository.deleteById(id);
                return new ApiResponse<>(true, "Expense deleted successfully", "Expense with ID " + id + " has been deleted");
            } else {
                return new ApiResponse<>(false, "Expense not found", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to delete expense: " + e.getMessage(), null);
        }
    }

    // Filter expenses by category
    public ApiResponse<List<ExpenseResponse>> getExpensesByCategory(ExpenseCategory category) {
        try {
            List<Expense> expenses = expenseRepository.findByCategory(category);
            List<ExpenseResponse> responses = expenses.stream()
                    .map(ExpenseResponse::new)
                    .collect(Collectors.toList());

            return new ApiResponse<>(true, "Expenses filtered by category successfully", responses);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to filter expenses by category: " + e.getMessage(), null);
        }
    }

    // Filter expenses by date range
    public ApiResponse<List<ExpenseResponse>> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            List<Expense> expenses = expenseRepository.findByExpenseDateBetween(startDate, endDate);
            List<ExpenseResponse> responses = expenses.stream()
                    .map(ExpenseResponse::new)
                    .collect(Collectors.toList());

            return new ApiResponse<>(true, "Expenses filtered by date range successfully", responses);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to filter expenses by date range: " + e.getMessage(), null);
        }
    }

    // Filter expenses by month
    public ApiResponse<List<ExpenseResponse>> getExpensesByMonth(int year, int month) {
        try {
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate startOfMonth = yearMonth.atDay(1);
            LocalDate startOfNextMonth = yearMonth.plusMonths(1).atDay(1);

            List<Expense> expenses = expenseRepository.findByMonth(startOfMonth, startOfNextMonth);
            List<ExpenseResponse> responses = expenses.stream()
                    .map(ExpenseResponse::new)
                    .collect(Collectors.toList());

            return new ApiResponse<>(true, "Expenses filtered by month successfully", responses);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to filter expenses by month: " + e.getMessage(), null);
        }
    }

    // Search expenses by title
    public ApiResponse<List<ExpenseResponse>> searchExpensesByTitle(String title) {
        try {
            List<Expense> expenses = expenseRepository.findByTitleContainingIgnoreCase(title);
            List<ExpenseResponse> responses = expenses.stream()
                    .map(ExpenseResponse::new)
                    .collect(Collectors.toList());

            return new ApiResponse<>(true, "Expenses searched successfully", responses);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to search expenses: " + e.getMessage(), null);
        }
    }

    // Get expense analytics
    public ApiResponse<ExpenseAnalyticsResponse> getExpenseAnalytics() {
        try {
            List<Expense> allExpenses = expenseRepository.findAll();
            ExpenseAnalyticsResponse analytics = calculateAnalytics(allExpenses);

            return new ApiResponse<>(true, "Analytics retrieved successfully", analytics);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve analytics: " + e.getMessage(), null);
        }
    }

    // Get expense analytics for a specific date range
    public ApiResponse<ExpenseAnalyticsResponse> getExpenseAnalyticsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            List<Expense> expenses = expenseRepository.findByExpenseDateBetween(startDate, endDate);
            ExpenseAnalyticsResponse analytics = calculateAnalytics(expenses);

            return new ApiResponse<>(true, "Analytics retrieved successfully for date range", analytics);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve analytics: " + e.getMessage(), null);
        }
    }

    // Helper method to calculate analytics
    private ExpenseAnalyticsResponse calculateAnalytics(List<Expense> expenses) {
        ExpenseAnalyticsResponse analytics = new ExpenseAnalyticsResponse();

        // Basic totals
        double totalExpenses = expenses.stream().mapToDouble(Expense::getAmount).sum();
        int totalTransactions = expenses.size();

        analytics.setTotalExpenses(totalExpenses);
        analytics.setTotalTransactions(totalTransactions);

        // Category breakdown
        Map<String, Double> categoryBreakdown = expenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getCategory().getDisplayName(),
                        Collectors.summingDouble(Expense::getAmount)
                ));
        analytics.setCategoryBreakdown(categoryBreakdown);

        // Monthly breakdown
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Double> monthlyBreakdown = expenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getExpenseDate().format(monthFormatter),
                        Collectors.summingDouble(Expense::getAmount)
                ));
        analytics.setMonthlyBreakdown(monthlyBreakdown);

        // Monthly trends
        Map<String, List<Expense>> monthlyGrouped = expenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getExpenseDate().format(monthFormatter)
                ));

        List<ExpenseAnalyticsResponse.MonthlyExpenseData> monthlyTrends = monthlyGrouped.entrySet().stream()
                .map(entry -> {
                    String month = entry.getKey();
                    List<Expense> monthExpenses = entry.getValue();
                    double amount = monthExpenses.stream().mapToDouble(Expense::getAmount).sum();
                    int count = monthExpenses.size();
                    return new ExpenseAnalyticsResponse.MonthlyExpenseData(month, amount, count);
                })
                .sorted(Comparator.comparing(ExpenseAnalyticsResponse.MonthlyExpenseData::getMonth))
                .collect(Collectors.toList());
        analytics.setMonthlyTrends(monthlyTrends);

        // Current month vs last month
        LocalDate now = LocalDate.now();
        String currentMonthStr = now.format(monthFormatter);
        String lastMonthStr = now.minusMonths(1).format(monthFormatter);

        List<Expense> currentMonthExpenses = expenses.stream()
                .filter(expense -> expense.getExpenseDate().format(monthFormatter).equals(currentMonthStr))
                .collect(Collectors.toList());

        List<Expense> lastMonthExpenses = expenses.stream()
                .filter(expense -> expense.getExpenseDate().format(monthFormatter).equals(lastMonthStr))
                .collect(Collectors.toList());

        double currentMonthTotal = currentMonthExpenses.stream().mapToDouble(Expense::getAmount).sum();
        double lastMonthTotal = lastMonthExpenses.stream().mapToDouble(Expense::getAmount).sum();

        analytics.setCurrentMonth(new ExpenseAnalyticsResponse.ExpenseSummary(currentMonthTotal, currentMonthExpenses.size()));
        analytics.setLastMonth(new ExpenseAnalyticsResponse.ExpenseSummary(lastMonthTotal, lastMonthExpenses.size()));

        return analytics;
    }

    // Get available categories
    public ApiResponse<List<String>> getAvailableCategories() {
        try {
            List<String> categories = Arrays.stream(ExpenseCategory.values())
                    .map(ExpenseCategory::getDisplayName)
                    .collect(Collectors.toList());

            return new ApiResponse<>(true, "Categories retrieved successfully", categories);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve categories: " + e.getMessage(), null);
        }
    }
}