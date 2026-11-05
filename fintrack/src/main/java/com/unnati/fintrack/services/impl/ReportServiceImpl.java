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

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public ReportServiceImpl(
            TransactionRepository transactionRepository,
            BudgetRepository budgetRepository) {

        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    // ---------------------------------------------------------
    // GET AUTHENTICATED USER EMAIL
    // ---------------------------------------------------------

    private String getCurrentUserEmail() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            throw new AccessDeniedException(
                    "You must be logged in to access reports."
            );
        }

        return authentication.getName();
    }

    // ---------------------------------------------------------
    // VALIDATE MONTH AND YEAR
    // ---------------------------------------------------------

    private void validateMonthAndYear(Integer month, Integer year) {

        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "Month must be between 1 and 12."
            );
        }

        if (year == null || year < 2000) {
            throw new IllegalArgumentException(
                    "Please provide a valid year."
            );
        }
    }

    private void validateYear(Integer year) {

        if (year == null || year < 2000) {
            throw new IllegalArgumentException(
                    "Please provide a valid year."
            );
        }
    }

    // ---------------------------------------------------------
    // FETCH ONLY LOGGED-IN USER'S TRANSACTIONS
    // ---------------------------------------------------------

    private List<Transaction> getTransactionsForMonth(
            Integer month,
            Integer year) {

        validateMonthAndYear(month, year);

        String email = getCurrentUserEmail();

        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return transactionRepository
                .findByUser_EmailAndTransactionDateBetween(
                        email,
                        startDate,
                        endDate
                );
    }

    // ---------------------------------------------------------
    // CALCULATE INCOME
    // ---------------------------------------------------------

    private BigDecimal calculateIncome(List<Transaction> transactions) {

        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ---------------------------------------------------------
    // CALCULATE EXPENSE
    // ---------------------------------------------------------

    private BigDecimal calculateExpense(List<Transaction> transactions) {

        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ---------------------------------------------------------
    // CALCULATE PERCENTAGE
    // ---------------------------------------------------------

    private BigDecimal calculatePercentage(
            BigDecimal value,
            BigDecimal total) {

        if (value == null
                || total == null
                || total.compareTo(BigDecimal.ZERO) == 0) {

            return BigDecimal.ZERO;
        }

        return value.multiply(new BigDecimal("100"))
                .divide(total, 2, RoundingMode.HALF_UP);
    }

    // ---------------------------------------------------------
    // CALCULATE MONTH-ON-MONTH CHANGE
    // ---------------------------------------------------------

    private BigDecimal calculatePercentageChange(
            BigDecimal previous,
            BigDecimal current) {

        if (previous == null
                || current == null
                || previous.compareTo(BigDecimal.ZERO) == 0) {

            return BigDecimal.ZERO;
        }

        return current.subtract(previous)
                .multiply(new BigDecimal("100"))
                .divide(previous, 2, RoundingMode.HALF_UP);
    }

    // =========================================================
    // 1. MONTHLY SUMMARY
    // =========================================================

    @Override
    public MonthlySummaryDto getMonthlySummary(
            Integer month,
            Integer year) {

        validateMonthAndYear(month, year);

        List<Transaction> transactions =
                getTransactionsForMonth(month, year);

        BigDecimal totalIncome = calculateIncome(transactions);
        BigDecimal totalExpense = calculateExpense(transactions);

        BigDecimal netSavings = totalIncome.subtract(totalExpense);
        BigDecimal totalAmount = totalIncome.add(totalExpense);

        long transactionCount = transactions.size();

        BigDecimal highestExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .filter(amount -> amount != null)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

        BigDecimal averageDailyExpense = totalExpense.divide(
                BigDecimal.valueOf(daysInMonth),
                2,
                RoundingMode.HALF_UP
        );

        YearMonth previousMonth = YearMonth.of(year, month).minusMonths(1);

        List<Transaction> previousTransactions =
                getTransactionsForMonth(
                        previousMonth.getMonthValue(),
                        previousMonth.getYear()
                );

        BigDecimal previousIncome = calculateIncome(previousTransactions);
        BigDecimal previousExpense = calculateExpense(previousTransactions);

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
    // 2. INCOME VS EXPENSE
    // =========================================================

    @Override
    public List<IncomeExpenseDto> getIncomeVsExpense(Integer year) {

        validateYear(year);

        List<IncomeExpenseDto> result = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {

            List<Transaction> transactions =
                    getTransactionsForMonth(month, year);

            BigDecimal income = calculateIncome(transactions);
            BigDecimal expense = calculateExpense(transactions);

            result.add(
                    IncomeExpenseDto.builder()
                            .month(month)
                            .year(year)
                            .totalIncome(income)
                            .totalExpense(expense)
                            .netSavings(income.subtract(expense))
                            .build()
            );
        }

        return result;
    }

    // =========================================================
    // 3. CATEGORY-WISE SPENDING
    // =========================================================

    @Override
    public List<CategorySpendingDto> getCategorySpending(
            Integer month,
            Integer year) {

        List<Transaction> expenses =
                getTransactionsForMonth(month, year)
                        .stream()
                        .filter(t -> t.getType() == TransactionType.EXPENSE)
                        .toList();

        BigDecimal totalExpense = calculateExpense(expenses);

        Map<String, BigDecimal> grouped = expenses.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCategory() == null
                                ? "Uncategorized"
                                : t.getCategory(),
                        LinkedHashMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add
                        )
                ));

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
    // 4. ACCOUNT-WISE SPENDING
    // =========================================================

    @Override
    public List<AccountSpendingDto> getAccountSpending(
            Integer month,
            Integer year) {

        List<Transaction> expenses =
                getTransactionsForMonth(month, year)
                        .stream()
                        .filter(t -> t.getType() == TransactionType.EXPENSE)
                        .toList();

        BigDecimal totalExpense = calculateExpense(expenses);

        Map<String, BigDecimal> grouped = expenses.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getAccount() == null
                                ? "Unknown Account"
                                : t.getAccount(),
                        LinkedHashMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add
                        )
                ));

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
    // 5. BUDGET PERFORMANCE
    // =========================================================

    @Override
    public List<BudgetPerformanceDto> getBudgetPerformance(
            Integer month,
            Integer year) {

        validateMonthAndYear(month, year);

        String email = getCurrentUserEmail();

        // Only budgets belonging to the authenticated user
        List<Budget> budgets =
                budgetRepository.findByUser_EmailAndMonthAndYear(
                        email,
                        month,
                        year
                );

        // Only transactions belonging to the authenticated user
        List<Transaction> transactions =
                getTransactionsForMonth(month, year);

        return budgets.stream()
                .map(budget -> {

                    BigDecimal spentAmount = transactions.stream()
                            .filter(t -> t.getType() == TransactionType.EXPENSE)
                            .filter(t ->
                                    budget.getCategory() != null
                                    && t.getCategory() != null
                                    && budget.getCategory().equalsIgnoreCase(
                                            t.getCategory()
                                    )
                            )
                            .map(Transaction::getAmount)
                            .filter(amount -> amount != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal budgetedAmount =
                            budget.getLimitAmount() == null
                                    ? BigDecimal.ZERO
                                    : budget.getLimitAmount();

                    BigDecimal progress = calculatePercentage(
                            spentAmount,
                            budgetedAmount
                    );

                    String status;

                    if (progress.compareTo(new BigDecimal("100")) > 0) {
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
    // 6. CSV EXPORT
    // =========================================================

    @Override
    public byte[] exportCsv(Integer month, Integer year) {

        validateMonthAndYear(month, year);

        MonthlySummaryDto summary = getMonthlySummary(month, year);

        List<CategorySpendingDto> categories =
                getCategorySpending(month, year);

        List<AccountSpendingDto> accounts =
                getAccountSpending(month, year);

        List<BudgetPerformanceDto> budgets =
                getBudgetPerformance(month, year);

        StringBuilder csv = new StringBuilder();

        csv.append("FINTRACK REPORT\n");
        csv.append("Month,").append(month).append("\n");
        csv.append("Year,").append(year).append("\n\n");

        csv.append("MONTHLY SUMMARY\n");
        csv.append("Metric,Amount\n");

        appendCsvRow(csv, "Total Amount", summary.getTotalAmount());
        appendCsvRow(csv, "Total Income", summary.getTotalIncome());
        appendCsvRow(csv, "Total Expense", summary.getTotalExpense());
        appendCsvRow(csv, "Net Savings", summary.getNetSavings());
        appendCsvRow(csv, "Average Daily Expense",
                summary.getAverageDailyExpense());
        appendCsvRow(csv, "Transaction Count",
                summary.getTransactionCount());
        appendCsvRow(csv, "Highest Expense",
                summary.getHighestExpense());

        csv.append("\nCATEGORY-WISE SPENDING\n");
        csv.append("Category,Expense Amount,Percentage\n");

        for (CategorySpendingDto category : categories) {
            appendCsvRow(
                    csv,
                    category.getCategory(),
                    category.getExpenseAmount(),
                    category.getPercentage() + "%"
            );
        }

        csv.append("\nACCOUNT-WISE SPENDING\n");
        csv.append("Account,Expense Amount,Percentage\n");

        for (AccountSpendingDto account : accounts) {
            appendCsvRow(
                    csv,
                    account.getAccount(),
                    account.getExpenseAmount(),
                    account.getPercentage() + "%"
            );
        }

        csv.append("\nBUDGET PERFORMANCE\n");
        csv.append("Budget,Budgeted,Spent,Progress,Status\n");

        for (BudgetPerformanceDto budget : budgets) {
            appendCsvRow(
                    csv,
                    budget.getBudgetName(),
                    budget.getBudgetedAmount(),
                    budget.getSpentAmount(),
                    budget.getProgressPercentage() + "%",
                    budget.getStatus()
            );
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    // ---------------------------------------------------------
    // CSV VALUE ESCAPING
    // ---------------------------------------------------------

    private void appendCsvRow(StringBuilder csv, Object... values) {

        for (int i = 0; i < values.length; i++) {

            if (i > 0) {
                csv.append(",");
            }

            String value = values[i] == null
                    ? ""
                    : values[i].toString();

            value = value.replace("\"", "\"\"");

            csv.append("\"").append(value).append("\"");
        }

        csv.append("\n");
    }
}