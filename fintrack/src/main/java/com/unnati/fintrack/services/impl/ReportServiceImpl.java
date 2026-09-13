package com.unnati.fintrack.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.unnati.fintrack.dto.response.AccountSpendingDto;
import com.unnati.fintrack.dto.response.BudgetPerformanceDto;
import com.unnati.fintrack.dto.response.CategorySpendingDto;
import com.unnati.fintrack.dto.response.IncomeExpenseDto;
import com.unnati.fintrack.dto.response.MonthlySummaryDto;
import com.unnati.fintrack.entity.Budget;
import com.unnati.fintrack.entity.Transaction;
import com.unnati.fintrack.enums.TransactionType;
import com.unnati.fintrack.repository.BudgetRepository;
import com.unnati.fintrack.repository.TransactionRepository;
import com.unnati.fintrack.services.ReportService;

@Service
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public ReportServiceImpl(
            TransactionRepository transactionRepository,
            BudgetRepository budgetRepository) {

        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    // =========================================================
    // MONTHLY SUMMARY
    // =========================================================

    @Override
    public MonthlySummaryDto getMonthlySummary(
            Integer month,
            Integer year) {

        List<Transaction> transactions =
                getTransactionsForMonth(month, year);

        BigDecimal totalIncome = calculateIncome(transactions);

        BigDecimal totalExpense = calculateExpense(transactions);

        BigDecimal netSavings =
                totalIncome.subtract(totalExpense);

        BigDecimal totalAmount =
                totalIncome.add(totalExpense);

        long transactionCount = transactions.size();

        BigDecimal highestExpense =
                transactions.stream()
                        .filter(t -> t.getType() == TransactionType.EXPENSE)
                        .map(Transaction::getAmount)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

        int daysInMonth =
                YearMonth.of(year, month).lengthOfMonth();

        BigDecimal averageDailyExpense =
                totalExpense.divide(
                        BigDecimal.valueOf(daysInMonth),
                        2,
                        RoundingMode.HALF_UP
                );

        YearMonth previousMonth =
                YearMonth.of(year, month).minusMonths(1);

        List<Transaction> previousTransactions =
                getTransactionsForMonth(
                        previousMonth.getMonthValue(),
                        previousMonth.getYear()
                );

        BigDecimal previousIncome =
                calculateIncome(previousTransactions);

        BigDecimal previousExpense =
                calculateExpense(previousTransactions);

        BigDecimal previousSavings =
                previousIncome.subtract(previousExpense);

        return MonthlySummaryDto.builder()
                .month(month)
                .year(year)
                .totalAmount(totalAmount)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netSavings(netSavings)
                .averageDailyExpense(averageDailyExpense)
                .transactionCount(transactionCount)
                .highestExpense(highestExpense)
                .incomeChangePercentage(
                        calculatePercentageChange(
                                previousIncome,
                                totalIncome
                        )
                )
                .expenseChangePercentage(
                        calculatePercentageChange(
                                previousExpense,
                                totalExpense
                        )
                )
                .savingsChangePercentage(
                        calculatePercentageChange(
                                previousSavings,
                                netSavings
                        )
                )
                .build();
    }

    // =========================================================
    // INCOME VS EXPENSE
    // =========================================================

    @Override
    public List<IncomeExpenseDto> getIncomeVsExpense(
            Integer year) {

        List<IncomeExpenseDto> result =
                new ArrayList<>();

        for (int month = 1; month <= 12; month++) {

            List<Transaction> transactions =
                    getTransactionsForMonth(month, year);

            BigDecimal income =
                    calculateIncome(transactions);

            BigDecimal expense =
                    calculateExpense(transactions);

            result.add(
                    IncomeExpenseDto.builder()
                            .month(month)
                            .year(year)
                            .totalIncome(income)
                            .totalExpense(expense)
                            .netSavings(
                                    income.subtract(expense)
                            )
                            .build()
            );
        }

        return result;
    }

    // =========================================================
    // CATEGORY-WISE SPENDING
    // =========================================================

    @Override
    public List<CategorySpendingDto> getCategorySpending(
            Integer month,
            Integer year) {

        List<Transaction> transactions =
                getTransactionsForMonth(month, year);

        List<Transaction> expenses =
                transactions.stream()
                        .filter(t ->
                                t.getType() ==
                                TransactionType.EXPENSE)
                        .toList();

        BigDecimal totalExpense =
                calculateExpense(expenses);

        Map<String, BigDecimal> grouped =
                expenses.stream()
                        .collect(
                                Collectors.groupingBy(
                                        Transaction::getCategory,
                                        LinkedHashMap::new,
                                        Collectors.reducing(
                                                BigDecimal.ZERO,
                                                Transaction::getAmount,
                                                BigDecimal::add
                                        )
                                )
                        );

        return grouped.entrySet()
                .stream()
                .map(entry ->
                        CategorySpendingDto.builder()
                                .category(entry.getKey())
                                .expenseAmount(entry.getValue())
                                .percentage(
                                        calculatePercentage(
                                                entry.getValue(),
                                                totalExpense
                                        )
                                )
                                .build()
                )
                .toList();
    }

    // =========================================================
    // ACCOUNT-WISE SPENDING
    // =========================================================

    @Override
    public List<AccountSpendingDto> getAccountSpending(
            Integer month,
            Integer year) {

        List<Transaction> transactions =
                getTransactionsForMonth(month, year);

        List<Transaction> expenses =
                transactions.stream()
                        .filter(t ->
                                t.getType() ==
                                TransactionType.EXPENSE)
                        .toList();

        BigDecimal totalExpense =
                calculateExpense(expenses);

        Map<String, BigDecimal> grouped =
                expenses.stream()
                        .collect(
                                Collectors.groupingBy(
                                        Transaction::getAccount,
                                        LinkedHashMap::new,
                                        Collectors.reducing(
                                                BigDecimal.ZERO,
                                                Transaction::getAmount,
                                                BigDecimal::add
                                        )
                                )
                        );

        return grouped.entrySet()
                .stream()
                .map(entry ->
                        AccountSpendingDto.builder()
                                .account(entry.getKey())
                                .expenseAmount(entry.getValue())
                                .percentage(
                                        calculatePercentage(
                                                entry.getValue(),
                                                totalExpense
                                        )
                                )
                                .build()
                )
                .toList();
    }

    // =========================================================
    // BUDGET PERFORMANCE
    // =========================================================

    @Override
    public List<BudgetPerformanceDto> getBudgetPerformance(
            Integer month,
            Integer year) {

        List<Budget> budgets =
                budgetRepository.findByMonthAndYear(
                        month,
                        year
                );

        List<Transaction> transactions =
                getTransactionsForMonth(month, year);

        return budgets.stream()
                .map(budget -> {

                    BigDecimal spentAmount =
                            transactions.stream()
                                    .filter(t ->
                                            t.getType() ==
                                            TransactionType.EXPENSE)
                                    .filter(t ->
                                            budget.getCategory()
                                                    .equalsIgnoreCase(
                                                            t.getCategory()
                                                    )
                                    )
                                    .map(Transaction::getAmount)
                                    .reduce(
                                            BigDecimal.ZERO,
                                            BigDecimal::add
                                    );

                    BigDecimal budgetedAmount =
                            budget.getLimitAmount();

                    BigDecimal progress =
                            calculatePercentage(
                                    spentAmount,
                                    budgetedAmount
                            );

                    String status;

                    if (progress.compareTo(
                            new BigDecimal("100")) > 0) {

                        status = "EXCEEDED";

                    } else if (progress.compareTo(
                            new BigDecimal("80")) >= 0) {

                        status = "WARNING";

                    } else {

                        status = "ON_TRACK";
                    }

                    return BudgetPerformanceDto.builder()
                            .budgetId(budget.getId())
                            .budgetName(budget.getBudgetName())
                            .budgetedAmount(budgetedAmount)
                            .spentAmount(spentAmount)
                            .progressPercentage(progress)
                            .status(status)
                            .build();
                })
                .toList();
    }

    // =========================================================
    // CSV EXPORT
    // =========================================================

    @Override
    public byte[] exportCsv(
            Integer month,
            Integer year) {

        MonthlySummaryDto summary =
                getMonthlySummary(month, year);

        List<CategorySpendingDto> categories =
                getCategorySpending(month, year);

        List<AccountSpendingDto> accounts =
                getAccountSpending(month, year);

        List<BudgetPerformanceDto> budgets =
                getBudgetPerformance(month, year);

        StringBuilder csv =
                new StringBuilder();

        csv.append("FINTRACK REPORT\n");
        csv.append("Month,").append(month).append("\n");
        csv.append("Year,").append(year).append("\n\n");

        csv.append("MONTHLY SUMMARY\n");

        csv.append("Total Amount,")
                .append(summary.getTotalAmount())
                .append("\n");

        csv.append("Total Income,")
                .append(summary.getTotalIncome())
                .append("\n");

        csv.append("Total Expense,")
                .append(summary.getTotalExpense())
                .append("\n");

        csv.append("Net Savings,")
                .append(summary.getNetSavings())
                .append("\n");

        csv.append("Average Daily Expense,")
                .append(summary.getAverageDailyExpense())
                .append("\n");

        csv.append("Transactions,")
                .append(summary.getTransactionCount())
                .append("\n");

        csv.append("Highest Expense,")
                .append(summary.getHighestExpense())
                .append("\n\n");

        csv.append("CATEGORY-WISE SPENDING\n");
        csv.append("Category,Expense Amount,Percentage\n");

        for (CategorySpendingDto category : categories) {

            csv.append(category.getCategory())
                    .append(",")
                    .append(category.getExpenseAmount())
                    .append(",")
                    .append(category.getPercentage())
                    .append("%\n");
        }

        csv.append("\nACCOUNT-WISE SPENDING\n");
        csv.append("Account,Expense Amount,Percentage\n");

        for (AccountSpendingDto account : accounts) {

            csv.append(account.getAccount())
                    .append(",")
                    .append(account.getExpenseAmount())
                    .append(",")
                    .append(account.getPercentage())
                    .append("%\n");
        }

        csv.append("\nBUDGET PERFORMANCE\n");
        csv.append(
                "Budget, Budgeted,Spent,Progress,Status\n"
        );

        for (BudgetPerformanceDto budget : budgets) {

            csv.append(budget.getBudgetName())
                    .append(",")
                    .append(budget.getBudgetedAmount())
                    .append(",")
                    .append(budget.getSpentAmount())
                    .append(",")
                    .append(budget.getProgressPercentage())
                    .append("%,")
                    .append(budget.getStatus())
                    .append("\n");
        }

        return csv.toString()
                .getBytes(StandardCharsets.UTF_8);
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private List<Transaction> getTransactionsForMonth(
            Integer month,
            Integer year) {

        YearMonth yearMonth =
                YearMonth.of(year, month);

        LocalDate startDate =
                yearMonth.atDay(1);

        LocalDate endDate =
                yearMonth.atEndOfMonth();

        return transactionRepository
                .findByTransactionDateBetween(
                        startDate,
                        endDate
                );
    }

    private BigDecimal calculateIncome(
            List<Transaction> transactions) {

        return transactions.stream()
                .filter(t ->
                        t.getType() ==
                        TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private BigDecimal calculateExpense(
            List<Transaction> transactions) {

        return transactions.stream()
                .filter(t ->
                        t.getType() ==
                        TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private BigDecimal calculatePercentage(
            BigDecimal value,
            BigDecimal total) {

        if (total == null ||
                total.compareTo(BigDecimal.ZERO) == 0) {

            return BigDecimal.ZERO;
        }

        return value
                .multiply(new BigDecimal("100"))
                .divide(
                        total,
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private BigDecimal calculatePercentageChange(
            BigDecimal previous,
            BigDecimal current) {

        if (previous == null ||
                previous.compareTo(BigDecimal.ZERO) == 0) {

            return BigDecimal.ZERO;
        }

        return current
                .subtract(previous)
                .multiply(new BigDecimal("100"))
                .divide(
                        previous,
                        2,
                        RoundingMode.HALF_UP
                );
    }
}