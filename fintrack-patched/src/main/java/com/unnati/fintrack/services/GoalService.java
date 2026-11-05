package com.unnati.fintrack.services;

import java.time.LocalDate;
import java.util.List;

import com.unnati.fintrack.entity.Goal;
import com.unnati.fintrack.enums.GoalPriority;
import com.unnati.fintrack.enums.GoalStatus;

public interface GoalService {

    List<Goal> findAll();

    Goal findById(Long id);

    Goal save(Goal goal);

    Goal update(Long id, Goal goal);

    void deleteById(Long id);

    List<Goal> findByPriority(GoalPriority priority);

    List<Goal> findByStatus(GoalStatus status);

    List<Goal> findByDeadline(LocalDate deadline);

    List<Goal> findByDeadlineBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    List<Goal> findByGoalName(String goalName);

    List<Goal> findByDeadlineBefore(LocalDate deadline);
}