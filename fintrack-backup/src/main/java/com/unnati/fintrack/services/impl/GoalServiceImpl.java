
package com.unnati.fintrack.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.unnati.fintrack.entity.Goal;
import com.unnati.fintrack.entity.User;
import com.unnati.fintrack.enums.GoalPriority;
import com.unnati.fintrack.enums.GoalStatus;
import com.unnati.fintrack.events.GoalCompletedEvent;
import com.unnati.fintrack.exception.ResourceNotFoundException;
import com.unnati.fintrack.repository.GoalRepository;
import com.unnati.fintrack.services.GoalService;
import com.unnati.fintrack.services.UserService;

@Service
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;

    public GoalServiceImpl(
            GoalRepository goalRepository,
            ApplicationEventPublisher eventPublisher,
            UserService userService) {

        this.goalRepository = goalRepository;
        this.eventPublisher = eventPublisher;
        this.userService = userService;
    }

    @Override
    public List<Goal> findAll() {
        return goalRepository.findAll();
    }

    @Override
    public Goal findById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Goal not found with id: " + id));
    }

    @Override
    public Goal save(Goal goal) {

        Goal savedGoal = goalRepository.save(goal);

        if (savedGoal.getStatus() == GoalStatus.COMPLETED) {
            publishGoalCompletedEvent(savedGoal);
        }

        return savedGoal;
    }

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

        Goal updatedGoal = goalRepository.save(existingGoal);

        if (updatedGoal.getStatus() == GoalStatus.COMPLETED) {
            publishGoalCompletedEvent(updatedGoal);
        }

        return updatedGoal;
    }

    private void publishGoalCompletedEvent(Goal goal) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException(
                    "Authenticated user is required to complete a goal event");
        }

        String email = authentication.getName();

        User user = userService.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email));

        eventPublisher.publishEvent(
                new GoalCompletedEvent(
                        goal.getId(),
                        goal.getGoalName(),
                        goal.getTargetAmount(),
                        goal.getSavedAmount(),
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                )
        );
    }

    @Override
    public void deleteById(Long id) {
        Goal existingGoal = findById(id);
        goalRepository.delete(existingGoal);
    }

    @Override
    public List<Goal> findByPriority(GoalPriority priority) {
        return goalRepository.findByPriority(priority);
    }

    @Override
    public List<Goal> findByStatus(GoalStatus status) {
        return goalRepository.findByStatus(status);
    }

    @Override
    public List<Goal> findByDeadline(LocalDate deadline) {
        return goalRepository.findByDeadline(deadline);
    }

    @Override
    public List<Goal> findByDeadlineBetween(
            LocalDate startDate,
            LocalDate endDate) {
        return goalRepository.findByDeadlineBetween(startDate, endDate);
    }

    @Override
    public List<Goal> findByDeadlineBefore(LocalDate deadline) {
        return goalRepository.findByDeadlineBefore(deadline);
    }

    @Override
    public List<Goal> findByGoalName(String goalName) {
        return goalRepository.findByGoalName(goalName);
    }
}
