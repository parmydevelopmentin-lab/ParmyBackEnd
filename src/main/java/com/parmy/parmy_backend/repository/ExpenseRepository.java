package com.parmy.parmy_backend.repository;

import com.parmy.parmy_backend.model.Expense;
import com.parmy.parmy_backend.model.ExpenseCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {
    
    // Find expenses by category
    List<Expense> findByCategory(ExpenseCategory category);
    
    // Find expenses by date range
    List<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find expenses by category and date range
    List<Expense> findByCategoryAndExpenseDateBetween(ExpenseCategory category, LocalDate startDate, LocalDate endDate);
    
    // Find expenses by title containing (case insensitive)
    List<Expense> findByTitleContainingIgnoreCase(String title);
    
    // Find expenses by created by user
    List<Expense> findByCreatedBy(String createdBy);
    
    // Find expenses by month and year
    @Query("{ 'expenseDate': { $gte: ?0, $lt: ?1 } }")
    List<Expense> findByMonth(LocalDate startOfMonth, LocalDate startOfNextMonth);
    
    // Find expenses by year
    @Query("{ 'expenseDate': { $gte: ?0, $lt: ?1 } }")
    List<Expense> findByYear(LocalDate startOfYear, LocalDate startOfNextYear);
    
    // Find expenses greater than amount
    List<Expense> findByAmountGreaterThan(double amount);
    
    // Find expenses less than amount
    List<Expense> findByAmountLessThan(double amount);
    
    // Find expenses between amount range
    List<Expense> findByAmountBetween(double minAmount, double maxAmount);
    
    // Custom aggregation query to get total by category
    @Query(value = "{ 'category': ?0 }", fields = "{ 'amount': 1 }")
    List<Expense> findAmountsByCategory(ExpenseCategory category);
    
    // Custom query to get expenses ordered by date descending
    List<Expense> findAllByOrderByExpenseDateDesc();
    
    // Custom query to get expenses ordered by amount descending
    List<Expense> findAllByOrderByAmountDesc();
    
    // Find recent expenses (last N days)
    @Query("{ 'expenseDate': { $gte: ?0 } }")
    List<Expense> findRecentExpenses(LocalDate fromDate);
}