package com.unnati.fintrack.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySummaryDto {

    private Integer month;
    private Integer year;

    private BigDecimal totalAmount;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netSavings;

    private BigDecimal averageDailyExpense;

    private Long transactionCount;

    private BigDecimal highestExpense;

    private BigDecimal incomeChangePercentage;
    private BigDecimal expenseChangePercentage;
    private BigDecimal savingsChangePercentage;
}