//package com.trivio.trivio_backend.config;
//
//import com.trivio.trivio_backend.model.Expense;
//import com.trivio.trivio_backend.model.ExpenseCategory;
//import com.trivio.trivio_backend.repository.ExpenseRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//@Order(2) // Run after the main DataInitializer
//public class ExpenseDataInitializer implements CommandLineRunner {
//
//    @Autowired
//    private ExpenseRepository expenseRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Only create dummy data if no expenses exist
//        if (expenseRepository.count() == 0) {
//            createDummyExpenses();
//            System.out.println("Dummy expense data has been created!");
//        } else {
//            System.out.println("Expense data already exists, skipping dummy data creation.");
//        }
//    }
//
//    private void createDummyExpenses() {
//        List<Expense> dummyExpenses = Arrays.asList(
//            // Current month expenses
//            createExpense("Software Development Team Salaries - December", ExpenseCategory.SALARY, 85000.00, LocalDate.now().minusDays(5), "Monthly salaries for 8 developers", "admin@trivioglobal.com"),
//            createExpense("Microsoft Office 365 Licenses", ExpenseCategory.SOFTWARE, 2400.00, LocalDate.now().minusDays(3), "Annual licenses for 50 users", "admin@trivioglobal.com"),
//            createExpense("New MacBook Pro for Design Team", ExpenseCategory.HARDWARE, 12500.00, LocalDate.now().minusDays(2), "3 MacBook Pro M3 for UI/UX designers", "admin@trivioglobal.com"),
//            createExpense("AWS Cloud Infrastructure", ExpenseCategory.SOFTWARE, 3200.00, LocalDate.now().minusDays(1), "Monthly cloud hosting and services", "admin@trivioglobal.com"),
//
//            // Last month expenses
//            createExpense("Management Team Salaries - November", ExpenseCategory.SALARY, 45000.00, LocalDate.now().minusMonths(1).minusDays(25), "Monthly salaries for management team", "admin@trivioglobal.com"),
//            createExpense("Office Rent - Hyderabad HQ", ExpenseCategory.OFFICE_RENT, 8500.00, LocalDate.now().minusMonths(1).minusDays(20), "Monthly rent for headquarters office", "admin@trivioglobal.com"),
//            createExpense("Electricity and Internet Bills", ExpenseCategory.UTILITIES, 1850.00, LocalDate.now().minusMonths(1).minusDays(18), "Monthly utility bills for all offices", "admin@trivioglobal.com"),
//            createExpense("Client Meeting Travel - Mumbai", ExpenseCategory.TRAVEL, 4200.00, LocalDate.now().minusMonths(1).minusDays(15), "Flight and accommodation for client presentation", "admin@trivioglobal.com"),
//            createExpense("Google Ads Campaign - Q4", ExpenseCategory.MARKETING, 8900.00, LocalDate.now().minusMonths(1).minusDays(10), "Digital marketing campaign for new product launch", "admin@trivioglobal.com"),
//            createExpense("Legal Consultation - Contract Review", ExpenseCategory.LEGAL, 2800.00, LocalDate.now().minusMonths(1).minusDays(8), "Legal review for major client contracts", "admin@trivioglobal.com"),
//
//            // 2 months ago
//            createExpense("Development Team Training - React Advanced", ExpenseCategory.TRAINING, 5500.00, LocalDate.now().minusMonths(2).minusDays(22), "Advanced React training for 10 developers", "admin@trivioglobal.com"),
//            createExpense("Dell Servers for Data Center", ExpenseCategory.HARDWARE, 22000.00, LocalDate.now().minusMonths(2).minusDays(20), "2 high-performance servers for data processing", "admin@trivioglobal.com"),
//            createExpense("Business Insurance Premium", ExpenseCategory.INSURANCE, 6800.00, LocalDate.now().minusMonths(2).minusDays(18), "Annual business insurance premium", "admin@trivioglobal.com"),
//            createExpense("Slack Premium + Jira Licenses", ExpenseCategory.SOFTWARE, 1200.00, LocalDate.now().minusMonths(2).minusDays(15), "Team collaboration tools for 50 users", "admin@trivioglobal.com"),
//            createExpense("Coffee Machine and Supplies", ExpenseCategory.MISCELLANEOUS, 850.00, LocalDate.now().minusMonths(2).minusDays(12), "Office coffee machine and monthly supplies", "admin@trivioglobal.com"),
//
//            // 3 months ago
//            createExpense("Senior Developer Salaries - September", ExpenseCategory.SALARY, 92000.00, LocalDate.now().minusMonths(3).minusDays(28), "Monthly salaries for senior development team", "admin@trivioglobal.com"),
//            createExpense("Office Maintenance and Cleaning", ExpenseCategory.MAINTENANCE, 1400.00, LocalDate.now().minusMonths(3).minusDays(25), "Monthly office maintenance and cleaning services", "admin@trivioglobal.com"),
//            createExpense("Conference Travel - TechCrunch Disrupt", ExpenseCategory.TRAVEL, 7200.00, LocalDate.now().minusMonths(3).minusDays(20), "Conference attendance for business development", "admin@trivioglobal.com"),
//            createExpense("Adobe Creative Suite Licenses", ExpenseCategory.SOFTWARE, 1800.00, LocalDate.now().minusMonths(3).minusDays(18), "Annual Creative Cloud licenses for design team", "admin@trivioglobal.com"),
//            createExpense("LinkedIn Premium and Sales Navigator", ExpenseCategory.MARKETING, 2400.00, LocalDate.now().minusMonths(3).minusDays(15), "Sales and marketing tools for lead generation", "admin@trivioglobal.com"),
//
//            // 4 months ago
//            createExpense("Office Furniture - Standing Desks", ExpenseCategory.HARDWARE, 4800.00, LocalDate.now().minusMonths(4).minusDays(25), "10 standing desks for developer workspace", "admin@trivioglobal.com"),
//            createExpense("Cybersecurity Training Program", ExpenseCategory.TRAINING, 3200.00, LocalDate.now().minusMonths(4).minusDays(20), "Security awareness training for all employees", "admin@trivioglobal.com"),
//            createExpense("Office Supplies and Stationery", ExpenseCategory.MISCELLANEOUS, 650.00, LocalDate.now().minusMonths(4).minusDays(18), "Monthly office supplies and stationery", "admin@trivioglobal.com"),
//            createExpense("Internet Upgrade - Fiber Connection", ExpenseCategory.UTILITIES, 2200.00, LocalDate.now().minusMonths(4).minusDays(15), "Upgrade to high-speed fiber internet", "admin@trivioglobal.com"),
//            createExpense("Legal Fees - Patent Filing", ExpenseCategory.LEGAL, 5500.00, LocalDate.now().minusMonths(4).minusDays(12), "Patent application for innovative solution", "admin@trivioglobal.com"),
//
//            // 5 months ago
//            createExpense("Marketing Team Salaries - July", ExpenseCategory.SALARY, 38000.00, LocalDate.now().minusMonths(5).minusDays(28), "Monthly salaries for marketing team", "admin@trivioglobal.com"),
//            createExpense("Client Entertainment - Dinner Meeting", ExpenseCategory.MISCELLANEOUS, 1200.00, LocalDate.now().minusMonths(5).minusDays(22), "Client relationship dinner meeting", "admin@trivioglobal.com"),
//            createExpense("Backup Server Maintenance", ExpenseCategory.MAINTENANCE, 1800.00, LocalDate.now().minusMonths(5).minusDays(20), "Quarterly maintenance for backup systems", "admin@trivioglobal.com"),
//            createExpense("Product Photography Equipment", ExpenseCategory.HARDWARE, 3400.00, LocalDate.now().minusMonths(5).minusDays(18), "Professional camera and lighting for product shots", "admin@trivioglobal.com"),
//            createExpense("Employee Health Insurance", ExpenseCategory.INSURANCE, 12000.00, LocalDate.now().minusMonths(5).minusDays(15), "Quarterly health insurance premium for employees", "admin@trivioglobal.com"),
//
//            // 6 months ago
//            createExpense("Docker and Kubernetes Training", ExpenseCategory.TRAINING, 4200.00, LocalDate.now().minusMonths(6).minusDays(25), "DevOps training for development team", "admin@trivioglobal.com"),
//            createExpense("Social Media Advertising Budget", ExpenseCategory.MARKETING, 6500.00, LocalDate.now().minusMonths(6).minusDays(20), "Facebook, Instagram, and Twitter ad campaigns", "admin@trivioglobal.com"),
//            createExpense("Emergency Generator Maintenance", ExpenseCategory.MAINTENANCE, 2200.00, LocalDate.now().minusMonths(6).minusDays(18), "Annual maintenance for backup power systems", "admin@trivioglobal.com"),
//            createExpense("Team Building Retreat", ExpenseCategory.MISCELLANEOUS, 8500.00, LocalDate.now().minusMonths(6).minusDays(15), "Annual team building retreat in Goa", "admin@trivioglobal.com")
//        );
//
//        expenseRepository.saveAll(dummyExpenses);
//    }
//
//    private Expense createExpense(String title, ExpenseCategory category, double amount, LocalDate expenseDate, String notes, String createdBy) {
//        Expense expense = new Expense();
//        expense.setTitle(title);
//        expense.setCategory(category);
//        expense.setAmount(amount);
//        expense.setExpenseDate(expenseDate);
//        expense.setNotes(notes);
//        expense.setCreatedBy(createdBy);
//        return expense;
//    }
//}