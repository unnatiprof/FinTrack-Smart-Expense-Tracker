package com.unnati.fintrack.services;

import java.time.LocalDate;
import java.util.List;

import com.unnati.fintrack.entity.Budget;
import com.unnati.fintrack.enums.BudgetStatus;

public interface BudgetService {

    List<Budget> findAll();

    Budget findById(Long id);

    Budget save(Budget budget);

    Budget update(Long id, Budget budget);

    void deleteById(Long id);

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