package com.unnati.fintrack.controllers;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unnati.fintrack.entity.Budget;
import com.unnati.fintrack.enums.BudgetStatus;
import com.unnati.fintrack.services.BudgetService;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // Get all budgets
    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets() {
        return ResponseEntity.ok(
                budgetService.findAll()
        );
    }

    // Get budget by ID
    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                budgetService.findById(id)
        );
    }

    // Create budget
    @PostMapping
    public ResponseEntity<Budget> createBudget(
            @RequestBody Budget budget) {

        Budget savedBudget = budgetService.save(budget);

        URI location = URI.create(
                "/api/budgets/" + savedBudget.getId()
        );

        return ResponseEntity
                .created(location)
                .body(savedBudget);
    }

    // Update budget
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(
            @PathVariable Long id,
            @RequestBody Budget budget) {

        return ResponseEntity.ok(
                budgetService.update(id, budget)
        );
    }

    // Delete budget
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long id) {

        budgetService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // Get budgets by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Budget>> getBudgetsByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                budgetService.findByCategory(category)
        );
    }

    // Get budgets by month
    @GetMapping("/month/{month}")
    public ResponseEntity<List<Budget>> getBudgetsByMonth(
            @PathVariable Integer month) {

        return ResponseEntity.ok(
                budgetService.findByMonth(month)
        );
    }

    // Get budgets by year
    @GetMapping("/year/{year}")
    public ResponseEntity<List<Budget>> getBudgetsByYear(
            @PathVariable Integer year) {

        return ResponseEntity.ok(
                budgetService.findByYear(year)
        );
    }

    // Get budgets by month and year
    @GetMapping("/period")
    public ResponseEntity<List<Budget>> getBudgetsByMonthAndYear(
            @RequestParam Integer month,
            @RequestParam Integer year) {

        return ResponseEntity.ok(
                budgetService.findByMonthAndYear(
                        month,
                        year
                )
        );
    }

    // Get budgets by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Budget>> getBudgetsByStatus(
            @PathVariable BudgetStatus status) {

        return ResponseEntity.ok(
                budgetService.findByStatus(status)
        );
    }

    // Get budgets by start date range
    @GetMapping("/date-range")
    public ResponseEntity<List<Budget>> getBudgetsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(
                budgetService.findByStartDateBetween(
                        startDate,
                        endDate
                )
        );
    }
}