package com.unnati.fintrack.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unnati.fintrack.entity.Goal;
import com.unnati.fintrack.enums.GoalPriority;
import com.unnati.fintrack.enums.GoalStatus;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    // Find all goals belonging to a user
    List<Goal> findByUser_Email(String email);

    // Find one goal only if it belongs to the user
    Optional<Goal> findByIdAndUser_Email(Long id, String email);

    // Filter by priority
    List<Goal> findByUser_EmailAndPriority(
            String email,
            GoalPriority priority
    );

    // Filter by status
    List<Goal> findByUser_EmailAndStatus(
            String email,
            GoalStatus status
    );

    // Filter by exact deadline
    List<Goal> findByUser_EmailAndDeadline(
            String email,
            LocalDate deadline
    );

    // Filter by deadline range
    List<Goal> findByUser_EmailAndDeadlineBetween(
            String email,
            LocalDate startDate,
            LocalDate endDate
    );

    // Search by goal name
    List<Goal> findByUser_EmailAndGoalNameContainingIgnoreCase(
            String email,
            String goalName
    );

    // Find goals with deadlines before a date
    List<Goal> findByUser_EmailAndDeadlineBefore(
            String email,
            LocalDate deadline
    );
}