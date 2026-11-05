package com.unnati.fintrack.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {

    private BigDecimal totalBalance;

    private BigDecimal totalIncome;

    private BigDecimal totalExpense;

    private Long totalUsers;

    private Long totalAccounts;

    private Long totalCategories;

    private Long totalTransactions;
}