package com.unnati.fintrack.events;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BudgetExceededEvent {

    private Long budgetId;
    private String budgetName;
    private BigDecimal limitAmount;
}