package com.unnati.fintrack.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.unnati.fintrack.entity.Goal;
import com.unnati.fintrack.enums.GoalPriority;
import com.unnati.fintrack.enums.GoalStatus;
import com.unnati.fintrack.events.GoalCompletedEvent;
import com.unnati.fintrack.exception.ResourceNotFoundException;
import com.unnati.fintrack.repository.GoalRepository;
import com.unnati.fintrack.services.GoalService;

@Service
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;

    private final ApplicationEventPublisher eventPublisher;

    public GoalServiceImpl(
            GoalRepository goalRepository,
            ApplicationEventPublisher eventPublisher) {

        this.goalRepository = goalRepository;
        this.eventPublisher = eventPublisher;
    }

    // =========================================================
    // GET ALL GOALS
    // =========================================================
    @Override
    public List<Goal> findAll() {
        return goalRepository.findAll();
    }

    // =========================================================
    // GET GOAL BY ID
    // =========================================================
    @Override
    public Goal findById(Long id) {

        return goalRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Goal not found with id: " + id
                        )
                );
    }

    // =========================================================
    // CREATE GOAL
    // =========================================================
    @Override
    public Goal save(Goal goal) {

        Goal savedGoal =
                goalRepository.save(goal);

        if (savedGoal.getStatus() == GoalStatus.COMPLETED) {

            eventPublisher.publishEvent(
                    new GoalCompletedEvent(
                            savedGoal.getId(),
                            savedGoal.getGoalName(),
                            savedGoal.getTargetAmount(),
                            savedGoal.getSavedAmount()
                    )
            );
        }

        return savedGoal;
    }

    // =========================================================
    // UPDATE GOAL
    // =========================================================
    @Override
    public Goal update(Long id, Goal goal) {

        Goal existingGoal = findById(id);

        existingGoal.setGoalName(goal.getGoalName());
        existingGoal.setTargetAmount(goal.getTargetAmount());
        existingGoal.setSavedAmount(goal.getSavedAmount());
        existingGoal.setDeadline(goal.getDeadline());
        existingGoal.setIcon(goal.getIcon());
        existingGoal.setPriority(goal.getPriority());
        existingGoal.setStatus(goal.getStatus());

        Goal updatedGoal =
                goalRepository.save(existingGoal);

        if (updatedGoal.getStatus() == GoalStatus.COMPLETED) {

            eventPublisher.publishEvent(
                    new GoalCompletedEvent(
                            updatedGoal.getId(),
                            updatedGoal.getGoalName(),
                            updatedGoal.getTargetAmount(),
                            updatedGoal.getSavedAmount()
                    )
            );
        }

        return updatedGoal;
    }

    // =========================================================
    // DELETE GOAL
    // =========================================================
    @Override
    public void deleteById(Long id) {

        Goal existingGoal = findById(id);

        goalRepository.delete(existingGoal);
    }

    // =========================================================
    // GET GOALS BY PRIORITY
    // =========================================================
    @Override
    public List<Goal> findByPriority(GoalPriority priority) {
        return goalRepository.findByPriority(priority);
    }

    // =========================================================
    // GET GOALS BY STATUS
    // =========================================================
    @Override
    public List<Goal> findByStatus(GoalStatus status) {
        return goalRepository.findByStatus(status);
    }

    // =========================================================
    // GET GOALS BY EXACT DEADLINE
    // =========================================================
    @Override
    public List<Goal> findByDeadline(LocalDate deadline) {
        return goalRepository.findByDeadline(deadline);
    }

    // =========================================================
    // GET GOALS BY DEADLINE RANGE
    // =========================================================
    @Override
    public List<Goal> findByDeadlineBetween(
            LocalDate startDate,
            LocalDate endDate) {

        return goalRepository.findByDeadlineBetween(
                startDate,
                endDate
        );
    }

    // =========================================================
    // GET GOALS BEFORE DEADLINE
    // =========================================================
    @Override
    public List<Goal> findByDeadlineBefore(LocalDate deadline) {

        return goalRepository.findByDeadlineBefore(deadline);
    }

    // =========================================================
    // GET GOALS BY NAME
    // =========================================================
    @Override
    public List<Goal> findByGoalName(String goalName) {
        return goalRepository.findByGoalName(goalName);
    }
}