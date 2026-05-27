package com.parmy.parmy_backend.dto;

import java.util.List;
import java.util.Map;

public class ExpenseAnalyticsResponse {
    private double totalExpenses;
    private int totalTransactions;
    private Map<String, Double> categoryBreakdown; // Category name -> Total amount
    private Map<String, Double> monthlyBreakdown; // Month (YYYY-MM) -> Total amount
    private List<MonthlyExpenseData> monthlyTrends;
    private ExpenseSummary currentMonth;
    private ExpenseSummary lastMonth;

    // Constructors
    public ExpenseAnalyticsResponse() {}

    // Inner classes for structured data
    public static class MonthlyExpenseData {
        private String month; // YYYY-MM format
        private double amount;
        private int count;

        public MonthlyExpenseData() {}

        public MonthlyExpenseData(String month, double amount, int count) {
            this.month = month;
            this.amount = amount;
            this.count = count;
        }

        // Getters and Setters
        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class ExpenseSummary {
        private double total;
        private int count;
        private double averagePerTransaction;

        public ExpenseSummary() {}

        public ExpenseSummary(double total, int count) {
            this.total = total;
            this.count = count;
            this.averagePerTransaction = count > 0 ? total / count : 0;
        }

        // Getters and Setters
        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public double getAveragePerTransaction() {
            return averagePerTransaction;
        }

        public void setAveragePerTransaction(double averagePerTransaction) {
            this.averagePerTransaction = averagePerTransaction;
        }
    }

    // Getters and Setters
    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public Map<String, Double> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(Map<String, Double> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }

    public Map<String, Double> getMonthlyBreakdown() {
        return monthlyBreakdown;
    }

    public void setMonthlyBreakdown(Map<String, Double> monthlyBreakdown) {
        this.monthlyBreakdown = monthlyBreakdown;
    }

    public List<MonthlyExpenseData> getMonthlyTrends() {
        return monthlyTrends;
    }

    public void setMonthlyTrends(List<MonthlyExpenseData> monthlyTrends) {
        this.monthlyTrends = monthlyTrends;
    }

    public ExpenseSummary getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(ExpenseSummary currentMonth) {
        this.currentMonth = currentMonth;
    }

    public ExpenseSummary getLastMonth() {
        return lastMonth;
    }

    public void setLastMonth(ExpenseSummary lastMonth) {
        this.lastMonth = lastMonth;
    }

    @Override
    public String toString() {
        return "ExpenseAnalyticsResponse{" +
                "totalExpenses=" + totalExpenses +
                ", totalTransactions=" + totalTransactions +
                ", categoryBreakdown=" + categoryBreakdown +
                ", monthlyBreakdown=" + monthlyBreakdown +
                ", monthlyTrends=" + monthlyTrends +
                ", currentMonth=" + currentMonth +
                ", lastMonth=" + lastMonth +
                '}';
    }
}