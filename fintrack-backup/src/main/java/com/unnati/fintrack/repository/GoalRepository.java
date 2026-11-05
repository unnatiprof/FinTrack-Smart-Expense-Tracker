package com.unnati.fintrack.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unnati.fintrack.entity.Goal;
import com.unnati.fintrack.enums.GoalPriority;
import com.unnati.fintrack.enums.GoalStatus;

public interface GoalRepository extends JpaRepository<Goal, Long> {

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