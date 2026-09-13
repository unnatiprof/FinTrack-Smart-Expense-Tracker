package com.unnati.fintrack.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unnati.fintrack.entity.Budget;
import com.unnati.fintrack.enums.BudgetStatus;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByCategory(String category);

    List<Budget> findByMonth(Integer month);

    List<Budget> findByYear(Integer year);

    List<Budget> findByMonthAndYear(Integer month, Integer year);

    List<Budget> findByStatus(BudgetStatus status);

    List<Budget> findByStartDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

}