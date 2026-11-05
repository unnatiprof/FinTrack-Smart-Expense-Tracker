package com.unnati.fintrack.controllers;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unnati.fintrack.dto.response.AccountSpendingDto;
import com.unnati.fintrack.dto.response.BudgetPerformanceDto;
import com.unnati.fintrack.dto.response.CategorySpendingDto;
import com.unnati.fintrack.dto.response.IncomeExpenseDto;
import com.unnati.fintrack.dto.response.MonthlySummaryDto;
import com.unnati.fintrack.services.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<MonthlySummaryDto> getMonthlySummary(
            @RequestParam Integer month,
            @RequestParam Integer year) {

        return ResponseEntity.ok(
                reportService.getMonthlySummary(month, year)
        );
    }

    @GetMapping("/income-vs-expense")
    public ResponseEntity<List<IncomeExpenseDto>> getIncomeVsExpense(
            @RequestParam Integer year) {

        return ResponseEntity.ok(
                reportService.getIncomeVsExpense(year)
        );
    }

    @GetMapping("/category-spending")
    public ResponseEntity<List<CategorySpendingDto>> getCategorySpending(
            @RequestParam Integer month,
            @RequestParam Integer year) {

        return ResponseEntity.ok(
                reportService.getCategorySpending(month, year)
        );
    }

    @GetMapping("/account-spending")
    public ResponseEntity<List<AccountSpendingDto>> getAccountSpending(
            @RequestParam Integer month,
            @RequestParam Integer year) {

        return ResponseEntity.ok(
                reportService.getAccountSpending(month, year)
        );
    }

    @GetMapping("/budget-performance")
    public ResponseEntity<List<BudgetPerformanceDto>> getBudgetPerformance(
            @RequestParam Integer month,
            @RequestParam Integer year) {

        return ResponseEntity.ok(
                reportService.getBudgetPerformance(month, year)
        );
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam Integer month,
            @RequestParam Integer year) {

        byte[] csv = reportService.exportCsv(month, year);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=fintrack-report-"
                                + month + "-" + year + ".csv"
                )
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}