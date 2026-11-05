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
public class BudgetPerformanceDto {

    private Long budgetId;

    private String budgetName;

    private BigDecimal budgetedAmount;

    private BigDecimal spentAmount;

    private BigDecimal progressPercentage;

    private String status;
}