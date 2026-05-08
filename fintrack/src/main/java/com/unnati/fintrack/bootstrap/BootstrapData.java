package com.unnati.fintrack.bootstrap;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner; // this interface is runned automatically after starting the bootstrap app. 
import org.springframework.stereotype.Component;// imports @Component

import com.unnati.fintrack.entity.Account;
import com.unnati.fintrack.entity.Category;
import com.unnati.fintrack.entity.Transaction;
import com.unnati.fintrack.entity.User;
import com.unnati.fintrack.enums.AccountStatus;
import com.unnati.fintrack.enums.AccountType;
import com.unnati.fintrack.enums.CategoryStatus;
import com.unnati.fintrack.enums.CategoryType;
import com.unnati.fintrack.enums.CurrencyType;
import com.unnati.fintrack.enums.PaymentMode;
import com.unnati.fintrack.enums.TransactionStatus;
import com.unnati.fintrack.enums.TransactionType;
import com.unnati.fintrack.enums.UserRole;
import com.unnati.fintrack.enums.UserStatus;
import com.unnati.fintrack.repository.AccountRepository;
import com.unnati.fintrack.repository.CategoryRepository;
import com.unnati.fintrack.repository.TransactionRepository;
import com.unnati.fintrack.repository.UserRepository;

@Component // this tells spring to create a component.
public class BootstrapData implements CommandLineRunner 
{

    private final UserRepository userRepository; // stores the object of the UserRepository
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    
    // constructor injection 
    public BootstrapData(UserRepository userRepository, AccountRepository accountRepository, CategoryRepository categoryRepository, TransactionRepository transactionRepository) 
    {
        this.userRepository = userRepository; // storing the value of the parameter into the current calling objects
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) 
    {

        if (userRepository.count() == 0) 
        {
            loadUsers(); // starts the method to load data if the object is empty
        }

        if (accountRepository.count() == 0) 
        {
            loadAccounts();
        }

        if (categoryRepository.count() == 0) 
        {
            loadCategories();
        }

        if (transactionRepository.count() == 0) 
        {
            loadTransactions();
        }

        System.out.println("Users Count: " + userRepository.count()); // returns the count of the  objects stored
        System.out.println("Accounts Count: " + accountRepository.count());
        System.out.println("Categories Count: " + categoryRepository.count());
        System.out.println("Transactions Count: " + transactionRepository.count());
    }

    private void loadUsers() 
    {
    	//Entity_class_name   obj_name =  Entity_class_name.builder().parameter1("").parameter2("value").parameter3("value").build();

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

        userRepository.save(user1); // RepositoryObjectname.save(EntityClassObject);  // saves the entity object data into the repository object 
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
    }

    private void loadAccounts() 
    {

        Account account1 = Account.builder()
                .accountName("Cash Wallet")
                .accountType(AccountType.CASH)
                .currentBalance(new BigDecimal("2500.00"))
                .currency(CurrencyType.INR)
                .status(AccountStatus.ACTIVE)
                .build();

        Account account2 = Account.builder()
                .accountName("SBI Bank Account")
                .accountType(AccountType.BANK_ACCOUNT)
                .currentBalance(new BigDecimal("35000.00"))
                .currency(CurrencyType.INR)
                .status(AccountStatus.ACTIVE)
                .build();

        Account account3 = Account.builder()
                .accountName("HDFC Credit Card")
                .accountType(AccountType.CREDIT_CARD)
                .currentBalance(new BigDecimal("15000.00"))
                .currency(CurrencyType.INR)
                .status(AccountStatus.ACTIVE)
                .build();

        Account account4 = Account.builder()
                .accountName("PhonePe Wallet")
                .accountType(AccountType.WALLET)
                .currentBalance(new BigDecimal("1200.00"))
                .currency(CurrencyType.INR)
                .status(AccountStatus.ACTIVE)
                .build();

        Account account5 = Account.builder()
                .accountName("Paytm Wallet")
                .accountType(AccountType.WALLET)
                .currentBalance(new BigDecimal("900.00"))
                .currency(CurrencyType.INR)
                .status(AccountStatus.ACTIVE)
                .build();

        accountRepository.save(account1);
        accountRepository.save(account2);
        accountRepository.save(account3);
        accountRepository.save(account4);
        accountRepository.save(account5);
    }

    private void loadCategories() 
    {

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

    private void loadTransactions() 
    {

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
                .build();

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
        transactionRepository.save(transaction4);
        transactionRepository.save(transaction5);
    }
}