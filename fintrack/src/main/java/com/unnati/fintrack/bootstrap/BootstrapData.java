
package com.unnati.fintrack.bootstrap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.unnati.fintrack.entity.Account;
import com.unnati.fintrack.entity.Budget;
import com.unnati.fintrack.entity.Category;
import com.unnati.fintrack.entity.Goal;
import com.unnati.fintrack.entity.Notification;
import com.unnati.fintrack.entity.Transaction;
import com.unnati.fintrack.entity.User;

import com.unnati.fintrack.enums.AccountStatus;
import com.unnati.fintrack.enums.AccountType;
import com.unnati.fintrack.enums.BudgetStatus;
import com.unnati.fintrack.enums.CategoryStatus;
import com.unnati.fintrack.enums.CategoryType;
import com.unnati.fintrack.enums.CurrencyType;
import com.unnati.fintrack.enums.GoalPriority;
import com.unnati.fintrack.enums.GoalStatus;
import com.unnati.fintrack.enums.NotificationStatus;
import com.unnati.fintrack.enums.NotificationType;
import com.unnati.fintrack.enums.PaymentMode;
import com.unnati.fintrack.enums.TransactionStatus;
import com.unnati.fintrack.enums.TransactionType;
import com.unnati.fintrack.enums.UserRole;
import com.unnati.fintrack.enums.UserStatus;

import com.unnati.fintrack.repository.AccountRepository;
import com.unnati.fintrack.repository.BudgetRepository;
import com.unnati.fintrack.repository.CategoryRepository;
import com.unnati.fintrack.repository.GoalRepository;
import com.unnati.fintrack.repository.NotificationRepository;
import com.unnati.fintrack.repository.TransactionRepository;
import com.unnati.fintrack.repository.UserRepository;

@Component
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final GoalRepository goalRepository;
    private final NotificationRepository notificationRepository;

    public BootstrapData(
            UserRepository userRepository,
            AccountRepository accountRepository,
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository,
            BudgetRepository budgetRepository,
            GoalRepository goalRepository,
            NotificationRepository notificationRepository) {

        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.goalRepository = goalRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void run(String... args) {

        if (userRepository.count() == 0) {
            loadUsers();
        }

        if (accountRepository.count() == 0) {
            loadAccounts();
        }

        if (categoryRepository.count() == 0) {
            loadCategories();
        }

        if (transactionRepository.count() == 0) {
            loadTransactions();
        }

        if (budgetRepository.count() == 0) {
            loadBudgets();
        }

        if (goalRepository.count() == 0) {
            loadGoals();
        }

        if (notificationRepository.count() == 0) {
            loadNotifications();
        }

        System.out.println("Users Count: " + userRepository.count());
        System.out.println("Accounts Count: " + accountRepository.count());
        System.out.println("Categories Count: " + categoryRepository.count());
        System.out.println("Transactions Count: " + transactionRepository.count());
        System.out.println("Budgets Count: " + budgetRepository.count());
        System.out.println("Goals Count: " + goalRepository.count());
        System.out.println("Notifications Count: " + notificationRepository.count());
    }

    // ---------------------------------------------------------
    // COMMON DEMO USER
    // ---------------------------------------------------------

    private User getDemoUser() {

        return userRepository.findByEmail("unnati@example.com")
                .orElseThrow(() ->
                        new RuntimeException("Demo user not found: unnati@example.com"));
    }

    // ---------------------------------------------------------
    // USERS
    // ---------------------------------------------------------

    private void loadUsers() {

        User user1 = User.builder()
                .name("Unnati Shri")
                .email("unnati@example.com")
                .password("password123")
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();

        User user2 = User.builder()
                .name("Aarav Sharma")
                .email("aarav@example.com")
                .password("password123")
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();

        User user3 = User.builder()
                .name("Priya Verma")
                .email("priya@example.com")
                .password("password123")
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();

        User user4 = User.builder()
                .name("Rohan Mehta")
                .email("rohan@example.com")
                .password("password123")
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();

        User user5 = User.builder()
                .name("Admin User")
                .email("admin@example.com")
                .password("admin123")
                .role(UserRole.ROLE_ADMIN)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
    }

    // ---------------------------------------------------------
    // ACCOUNTS
    // ---------------------------------------------------------

    private void loadAccounts() {

        User demoUser = getDemoUser();

        Account account1 = Account.builder()
                .accountName("Cash Wallet")
                .accountType(AccountType.CASH)
                .currentBalance(new BigDecimal("2500.00"))
                .currency(CurrencyType.INR)
                .status(AccountStatus.ACTIVE)
                .user(demoUser)
                .build();

        Account account2 = Account.builder()
                .accountName("SBI Bank Account")
                .accountType(AccountType.BANK_ACCOUNT)
                .currentBalance(new BigDecimal("35000.00"))
                .currency(CurrencyType.INR)
                .status(AccountStatus.ACTIVE)
                .user(demoUser)
                .build();

        Account account3 = Account.builder()
                .accountName("HDFC Credit Card")
                .accountType(AccountType.CREDIT_CARD)
                .currentBalance(new BigDecimal("15000.00"))
                .currency(CurrencyType.INR)
                .status(AccountStatus.ACTIVE)
                .user(demoUser)
                .build();

        Account account4 = Account.builder()
                .accountName("PhonePe Wallet")
                .accountType(AccountType.WALLET)
                .currentBalance(new BigDecimal("1200.00"))
                .currency(CurrencyType.INR)
                .status(AccountStatus.ACTIVE)
                .user(demoUser)
                .build();

        Account account5 = Account.builder()
                .accountName("Paytm Wallet")
                .accountType(AccountType.WALLET)
                .currentBalance(new BigDecimal("900.00"))
                .currency(CurrencyType.INR)
                .status(AccountStatus.ACTIVE)
                .user(demoUser)
                .build();

        accountRepository.save(account1);
        accountRepository.save(account2);
        accountRepository.save(account3);
        accountRepository.save(account4);
        accountRepository.save(account5);
    }

    // ---------------------------------------------------------
    // CATEGORIES
    // ---------------------------------------------------------

    private void loadCategories() {

        Category category1 = Category.builder()
                .name("Food")
                .category(CategoryType.EXPENSE)
                .colorCode("#FF7043")
                .icon("restaurant")
                .status(CategoryStatus.ACTIVE)
                .build();

        Category category2 = Category.builder()
                .name("Travel")
                .category(CategoryType.EXPENSE)
                .colorCode("#42A5F5")
                .icon("directions_bus")
                .status(CategoryStatus.ACTIVE)
                .build();

        Category category3 = Category.builder()
                .name("Shopping")
                .category(CategoryType.EXPENSE)
                .colorCode("#AB47BC")
                .icon("shopping_bag")
                .status(CategoryStatus.ACTIVE)
                .build();

        Category category4 = Category.builder()
                .name("Education")
                .category(CategoryType.EXPENSE)
                .colorCode("#66BB6A")
                .icon("school")
                .status(CategoryStatus.ACTIVE)
                .build();

        Category category5 = Category.builder()
                .name("Hostel")
                .category(CategoryType.EXPENSE)
                .colorCode("#FFA726")
                .icon("home")
                .status(CategoryStatus.ACTIVE)
                .build();

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        categoryRepository.save(category4);
        categoryRepository.save(category5);
    }

    // ---------------------------------------------------------
    // TRANSACTIONS
    // ---------------------------------------------------------

    private void loadTransactions() {

        User demoUser = getDemoUser();

        Transaction transaction1 = Transaction.builder()
                .title("College Canteen Lunch")
                .description("Lunch at college canteen")
                .amount(new BigDecimal("120.00"))
                .type(TransactionType.EXPENSE)
                .category("Food")
                .account("Cash Wallet")
                .paymentMode(PaymentMode.CASH)
                .transactionDate(LocalDate.now().minusDays(1))
                .transactionStatus(TransactionStatus.COMPLETED)
                .notes("Regular lunch expense")
                .user(demoUser)
                .build();

        Transaction transaction2 = Transaction.builder()
                .title("Metro Recharge")
                .description("Monthly metro card recharge")
                .amount(new BigDecimal("500.00"))
                .type(TransactionType.EXPENSE)
                .category("Travel")
                .account("SBI Bank Account")
                .paymentMode(PaymentMode.UPI)
                .transactionDate(LocalDate.now().minusDays(2))
                .transactionStatus(TransactionStatus.COMPLETED)
                .notes("Travel expense")
                .user(demoUser)
                .build();

        Transaction transaction3 = Transaction.builder()
                .title("Notebook Purchase")
                .description("Bought notebooks and pens")
                .amount(new BigDecimal("250.00"))
                .type(TransactionType.EXPENSE)
                .category("Education")
                .account("Cash Wallet")
                .paymentMode(PaymentMode.CASH)
                .transactionDate(LocalDate.now().minusDays(3))
                .transactionStatus(TransactionStatus.COMPLETED)
                .notes("College stationery")
                .user(demoUser)
                .build();

        Transaction transaction4 = Transaction.builder()
                .title("Online Shopping")
                .description("Bought clothes online")
                .amount(new BigDecimal("1499.00"))
                .type(TransactionType.EXPENSE)
                .category("Shopping")
                .account("HDFC Credit Card")
                .paymentMode(PaymentMode.CARD)
                .transactionDate(LocalDate.now().minusDays(4))
                .transactionStatus(TransactionStatus.COMPLETED)
                .notes("Shopping expense")
                .user(demoUser)
                .build();

        Transaction transaction5 = Transaction.builder()
                .title("Hostel Mess Payment")
                .description("Monthly mess payment")
                .amount(new BigDecimal("3000.00"))
                .type(TransactionType.EXPENSE)
                .category("Hostel")
                .account("PhonePe Wallet")
                .paymentMode(PaymentMode.UPI)
                .transactionDate(LocalDate.now().minusDays(5))
                .transactionStatus(TransactionStatus.COMPLETED)
                .notes("Hostel mess bill")
                .user(demoUser)
                .build();

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
        transactionRepository.save(transaction4);
        transactionRepository.save(transaction5);
    }

    // ---------------------------------------------------------
    // BUDGETS
    // ---------------------------------------------------------

    private void loadBudgets() {

        User demoUser = getDemoUser();

        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        Budget budget1 = Budget.builder()
                .budgetName("Food")
                .category("Food")
                .month(startDate.getMonthValue())
                .year(startDate.getYear())
                .limitAmount(new BigDecimal("15000.00"))
                .startDate(startDate)
                .endDate(endDate)
                .status(BudgetStatus.INACTIVE)
                .user(demoUser)
                .build();

        Budget budget2 = Budget.builder()
                .budgetName("Shopping")
                .category("Shopping")
                .month(startDate.getMonthValue())
                .year(startDate.getYear())
                .limitAmount(new BigDecimal("25000.00"))
                .startDate(startDate)
                .endDate(endDate)
                .status(BudgetStatus.COMPLETED)
                .user(demoUser)
                .build();

        Budget budget3 = Budget.builder()
                .budgetName("Travel")
                .category("Travel")
                .month(startDate.getMonthValue())
                .year(startDate.getYear())
                .limitAmount(new BigDecimal("20000.00"))
                .startDate(startDate)
                .endDate(endDate)
                .status(BudgetStatus.EXCEEDED)
                .user(demoUser)
                .build();

        Budget budget4 = Budget.builder()
                .budgetName("Utilities")
                .category("Utilities")
                .month(startDate.getMonthValue())
                .year(startDate.getYear())
                .limitAmount(new BigDecimal("10000.00"))
                .startDate(startDate)
                .endDate(endDate)
                .status(BudgetStatus.COMPLETED)
                .user(demoUser)
                .build();

        Budget budget5 = Budget.builder()
                .budgetName("Education")
                .category("Education")
                .month(startDate.getMonthValue())
                .year(startDate.getYear())
                .limitAmount(new BigDecimal("12000.00"))
                .startDate(startDate)
                .endDate(endDate)
                .status(BudgetStatus.COMPLETED)
                .user(demoUser)
                .build();

        budgetRepository.save(budget1);
        budgetRepository.save(budget2);
        budgetRepository.save(budget3);
        budgetRepository.save(budget4);
        budgetRepository.save(budget5);
    }

    // ---------------------------------------------------------
    // GOALS
    // ---------------------------------------------------------

    private void loadGoals() {

        User demoUser = getDemoUser();

        Goal goal1 = Goal.builder()
                .goalName("Laptop Fund")
                .targetAmount(new BigDecimal("80000.00"))
                .savedAmount(new BigDecimal("53000.00"))
                .deadline(LocalDate.of(2025, 6, 30))
                .icon("laptop")
                .priority(GoalPriority.HIGH)
                .status(GoalStatus.IN_PROGRESS)
                .user(demoUser)
                .build();

        Goal goal2 = Goal.builder()
                .goalName("Europe Trip")
                .targetAmount(new BigDecimal("800000.00"))
                .savedAmount(new BigDecimal("320000.00"))
                .deadline(LocalDate.of(2025, 12, 15))
                .icon("flight")
                .priority(GoalPriority.MEDIUM)
                .status(GoalStatus.IN_PROGRESS)
                .user(demoUser)
                .build();

        Goal goal3 = Goal.builder()
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("500000.00"))
                .savedAmount(new BigDecimal("450000.00"))
                .deadline(LocalDate.of(2025, 12, 31))
                .icon("security")
                .priority(GoalPriority.HIGH)
                .status(GoalStatus.IN_PROGRESS)
                .user(demoUser)
                .build();

        Goal goal4 = Goal.builder()
                .goalName("Home Down Payment")
                .targetAmount(new BigDecimal("3500000.00"))
                .savedAmount(new BigDecimal("640000.00"))
                .deadline(LocalDate.of(2027, 12, 31))
                .icon("home")
                .priority(GoalPriority.LOW)
                .status(GoalStatus.IN_PROGRESS)
                .user(demoUser)
                .build();

        goalRepository.save(goal1);
        goalRepository.save(goal2);
        goalRepository.save(goal3);
        goalRepository.save(goal4);
    }

    // ---------------------------------------------------------
    // NOTIFICATIONS
    // ---------------------------------------------------------

    private void loadNotifications() {

        User demoUser = getDemoUser();

        Notification notification1 = new Notification();
        notification1.setTitle("Budget Limit Warning");
        notification1.setMessage("You have used 89% of your Shopping budget.");
        notification1.setType(NotificationType.BUDGET);
        notification1.setStatus(NotificationStatus.UNREAD);
        notification1.setCreatedAt(LocalDateTime.now().minusHours(2));
        notification1.setActionLabel("Review Budget");
        notification1.setActionUrl("/api/budgets/2");
        notification1.setUser(demoUser);

        Notification notification2 = new Notification();
        notification2.setTitle("Budget Exceeded");
        notification2.setMessage("Your Travel budget has exceeded its limit.");
        notification2.setType(NotificationType.BUDGET);
        notification2.setStatus(NotificationStatus.UNREAD);
        notification2.setCreatedAt(LocalDateTime.now().minusHours(5));
        notification2.setActionLabel("Review Budget");
        notification2.setActionUrl("/api/budgets/3");
        notification2.setUser(demoUser);

        Notification notification3 = new Notification();
        notification3.setTitle("Goal Progress Update");
        notification3.setMessage("Your Emergency Fund is 90% complete. Keep going!");
        notification3.setType(NotificationType.GOAL);
        notification3.setStatus(NotificationStatus.UNREAD);
        notification3.setCreatedAt(LocalDateTime.now().minusDays(1));
        notification3.setActionLabel("View Goal");
        notification3.setActionUrl("/api/goals/3");
        notification3.setUser(demoUser);

        Notification notification4 = new Notification();
        notification4.setTitle("New Login Detected");
        notification4.setMessage("A new login to your FinTrack account was detected.");
        notification4.setType(NotificationType.SECURITY);
        notification4.setStatus(NotificationStatus.READ);
        notification4.setCreatedAt(LocalDateTime.now().minusDays(1).minusHours(3));
        notification4.setActionLabel("Review Security");
        notification4.setActionUrl("/api/users/profile");
        notification4.setUser(demoUser);

        Notification notification5 = new Notification();
        notification5.setTitle("Monthly Report Ready");
        notification5.setMessage("Your latest monthly spending report is ready to review.");
        notification5.setType(NotificationType.SYSTEM);
        notification5.setStatus(NotificationStatus.READ);
        notification5.setCreatedAt(LocalDateTime.now().minusDays(2));
        notification5.setActionLabel("View Report");
        notification5.setActionUrl("/api/reports");
        notification5.setUser(demoUser);

        Notification notification6 = new Notification();
        notification6.setTitle("Budget Reminder");
        notification6.setMessage("You are approaching the limit of your Food budget.");
        notification6.setType(NotificationType.BUDGET);
        notification6.setStatus(NotificationStatus.UNREAD);
        notification6.setCreatedAt(LocalDateTime.now().minusDays(2).minusHours(4));
        notification6.setActionLabel("Review Budget");
        notification6.setActionUrl("/api/budgets/1");
        notification6.setUser(demoUser);

        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        notificationRepository.save(notification3);
        notificationRepository.save(notification4);
        notificationRepository.save(notification5);
        notificationRepository.save(notification6);
    }
}
