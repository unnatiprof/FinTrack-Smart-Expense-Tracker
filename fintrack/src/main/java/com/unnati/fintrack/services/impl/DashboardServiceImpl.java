package com.unnati.fintrack.services.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.unnati.fintrack.dto.response.DashboardSummaryDto;
import com.unnati.fintrack.entity.Account;
import com.unnati.fintrack.entity.Transaction;
import com.unnati.fintrack.enums.TransactionType;
import com.unnati.fintrack.repository.AccountRepository;
import com.unnati.fintrack.repository.CategoryRepository;
import com.unnati.fintrack.repository.TransactionRepository;
import com.unnati.fintrack.repository.UserRepository;
import com.unnati.fintrack.services.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public DashboardServiceImpl(UserRepository userRepository,
                                AccountRepository accountRepository,
                                CategoryRepository categoryRepository,
                                TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public DashboardSummaryDto getSummary() {
        BigDecimal totalBalance = calculateTotalBalance();
        BigDecimal totalIncome = calculateTotalIncome();
        BigDecimal totalExpense = calculateTotalExpense();

        return new DashboardSummaryDto(
                totalBalance,
                totalIncome,
                totalExpense,
                userRepository.count(),
                accountRepository.count(),
                categoryRepository.count(),
                transactionRepository.count()
        );
    }

    private BigDecimal calculateTotalBalance() {
        List<Account> accounts = accountRepository.findAll();

        BigDecimal totalBalance = BigDecimal.ZERO;

        for (Account account : accounts) {
            if (account.getCurrentBalance() != null) {
                totalBalance = totalBalance.add(account.getCurrentBalance());
            }
        }

        return totalBalance;
    }

    private BigDecimal calculateTotalIncome() {
        List<Transaction> transactions = transactionRepository.findAll();

        BigDecimal totalIncome = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME && transaction.getAmount() != null) {
                totalIncome = totalIncome.add(transaction.getAmount());
            }
        }

        return totalIncome;
    }

    private BigDecimal calculateTotalExpense() {
        List<Transaction> transactions = transactionRepository.findAll();

        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.EXPENSE && transaction.getAmount() != null) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }

        return totalExpense;
    }
}