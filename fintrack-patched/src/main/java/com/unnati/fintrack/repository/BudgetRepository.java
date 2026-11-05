package com.unnati.fintrack.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unnati.fintrack.entity.Budget;
import com.unnati.fintrack.enums.BudgetStatus;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    // Find budgets belonging to a particular user
    List<Budget> findByUser_Email(String email);

    Optional<Budget> findByIdAndUser_Email(Long id, String email);

    // Filter user's budgets by category
    List<Budget> findByUser_EmailAndCategoryIgnoreCase(
            String email,
            String category
    );

    // Filter user's budgets by month
    List<Budget> findByUser_EmailAndMonth(
            String email,
            Integer month
    );

    // Filter user's budgets by year
    List<Budget> findByUser_EmailAndYear(
            String email,
            Integer year
    );

    // Filter user's budgets by month and year
    List<Budget> findByUser_EmailAndMonthAndYear(
            String email,
            Integer month,
            Integer year
    );

    // Filter user's budgets by status
    List<Budget> findByUser_EmailAndStatus(
            String email,
            BudgetStatus status
    );

    // Filter user's budgets by start date range
    List<Budget> findByUser_EmailAndStartDateBetween(
            String email,
            LocalDate startDate,
            LocalDate endDate
    );
    
    
}