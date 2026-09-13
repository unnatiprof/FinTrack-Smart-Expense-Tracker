package com.unnati.fintrack.services;

import java.util.List;

import com.unnati.fintrack.dto.response.AccountSpendingDto;
import com.unnati.fintrack.dto.response.BudgetPerformanceDto;
import com.unnati.fintrack.dto.response.CategorySpendingDto;
import com.unnati.fintrack.dto.response.IncomeExpenseDto;
import com.unnati.fintrack.dto.response.MonthlySummaryDto;

public interface ReportService {

    MonthlySummaryDto getMonthlySummary(
            Integer month,
            Integer year
    );

    List<IncomeExpenseDto> getIncomeVsExpense(
            Integer year
    );

    List<CategorySpendingDto> getCategorySpending(
            Integer month,
            Integer year
    );

    List<AccountSpendingDto> getAccountSpending(
            Integer month,
            Integer year
    );

    List<BudgetPerformanceDto> getBudgetPerformance(
            Integer month,
            Integer year
    );

    byte[] exportCsv(
            Integer month,
            Integer year
    );
}