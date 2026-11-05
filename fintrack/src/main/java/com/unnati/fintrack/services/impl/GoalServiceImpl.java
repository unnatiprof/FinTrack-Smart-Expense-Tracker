package com.unnati.fintrack.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
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

    // Get authenticated user's email from JWT authentication
    private String getCurrentUserEmail() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            throw new AccessDeniedException(
                    "User not authenticated"
            );
        }

        return authentication.getName();
    }

    // Find a goal only if it belongs to the authenticated user
    private Goal findOwnedGoal(Long id, String email) {

        return goalRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Goal not found with id: " + id
                        ));
    }

    // Validate goal details
    private void validateGoal(Goal goal) {

        if (goal.getGoalName() == null
                || goal.getGoalName().isBlank()) {

            throw new IllegalArgumentException(
                    "Goal name is required"
            );
        }

        if (goal.getTargetAmount() == null
                || goal.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Target amount must be greater than zero"
            );
        }

        if (goal.getSavedAmount() == null
                || goal.getSavedAmount().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Saved amount cannot be negative"
            );
        }

        if (goal.getDeadline() == null) {

            throw new IllegalArgumentException(
                    "Goal deadline is required"
            );
        }

        if (goal.getPriority() == null) {

            throw new IllegalArgumentException(
                    "Goal priority is required"
            );
        }

        if (goal.getStatus() == null) {

            throw new IllegalArgumentException(
                    "Goal status is required"
            );
        }
    }

    // Publish goal completion event for the correct user
    private void publishGoalCompletedEvent(
            Goal goal,
            User user) {

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

    // Get all goals belonging to authenticated user
    @Override
    @Transactional(readOnly = true)
    public List<Goal> findAll() {

        String email = getCurrentUserEmail();

        return goalRepository.findByUser_Email(email);
    }

    // Get goal by ID, only if owned by authenticated user
    @Override
    @Transactional(readOnly = true)
    public Goal findById(Long id) {

        String email = getCurrentUserEmail();

        return findOwnedGoal(id, email);
    }

    // Create goal for authenticated user
    @Override
    public Goal save(Goal goal) {

        String email = getCurrentUserEmail();

        User user = userService.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        ));

        validateGoal(goal);

        // Never accept an ID supplied by the client
        goal.setId(null);

        // Assign owner from JWT-authenticated user
        goal.setUser(user);

        goal.setGoalName(goal.getGoalName().trim());

        Goal savedGoal = goalRepository.save(goal);

        // Publish event if the newly created goal is completed
        if (savedGoal.getStatus() == GoalStatus.COMPLETED) {

            publishGoalCompletedEvent(savedGoal, user);
        }

        return savedGoal;
    }

    // Update only the authenticated user's goal
    @Override
    public Goal update(Long id, Goal goal) {

        String email = getCurrentUserEmail();

        Goal existingGoal = findOwnedGoal(id, email);

        validateGoal(goal);

        boolean wasAlreadyCompleted =
                existingGoal.getStatus() == GoalStatus.COMPLETED;

        existingGoal.setGoalName(goal.getGoalName().trim());
        existingGoal.setTargetAmount(goal.getTargetAmount());
        existingGoal.setSavedAmount(goal.getSavedAmount());
        existingGoal.setDeadline(goal.getDeadline());
        existingGoal.setIcon(goal.getIcon());
        existingGoal.setPriority(goal.getPriority());
        existingGoal.setStatus(goal.getStatus());

        Goal updatedGoal = goalRepository.save(existingGoal);

        boolean isNowCompleted =
                updatedGoal.getStatus() == GoalStatus.COMPLETED;

        // Publish only on transition to COMPLETED
        if (!wasAlreadyCompleted && isNowCompleted) {

            publishGoalCompletedEvent(
                    updatedGoal,
                    existingGoal.getUser()
            );
        }

        return updatedGoal;
    }

    // Delete only the authenticated user's goal
    @Override
    public void deleteById(Long id) {

        String email = getCurrentUserEmail();

        Goal existingGoal = findOwnedGoal(id, email);

        goalRepository.delete(existingGoal);
    }

    // Filter user's goals by priority
    @Override
    @Transactional(readOnly = true)
    public List<Goal> findByPriority(GoalPriority priority) {

        String email = getCurrentUserEmail();

        if (priority == null) {
            throw new IllegalArgumentException(
                    "Goal priority is required"
            );
        }

        return goalRepository.findByUser_EmailAndPriority(
                email,
                priority
        );
    }

    // Filter user's goals by status
    @Override
    @Transactional(readOnly = true)
    public List<Goal> findByStatus(GoalStatus status) {

        String email = getCurrentUserEmail();

        if (status == null) {
            throw new IllegalArgumentException(
                    "Goal status is required"
            );
        }

        return goalRepository.findByUser_EmailAndStatus(
                email,
                status
        );
    }

    // Find user's goals by exact deadline
    @Override
    @Transactional(readOnly = true)
    public List<Goal> findByDeadline(LocalDate deadline) {

        String email = getCurrentUserEmail();

        if (deadline == null) {
            throw new IllegalArgumentException(
                    "Deadline is required"
            );
        }

        return goalRepository.findByUser_EmailAndDeadline(
                email,
                deadline
        );
    }

    // Find user's goals within a deadline range
    @Override
    @Transactional(readOnly = true)
    public List<Goal> findByDeadlineBetween(
            LocalDate startDate,
            LocalDate endDate) {

        String email = getCurrentUserEmail();

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Start date and end date are required"
            );
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }

        return goalRepository.findByUser_EmailAndDeadlineBetween(
                email,
                startDate,
                endDate
        );
    }

    // Search user's goals by name
    @Override
    @Transactional(readOnly = true)
    public List<Goal> findByGoalName(String goalName) {

        String email = getCurrentUserEmail();

        if (goalName == null || goalName.isBlank()) {
            throw new IllegalArgumentException(
                    "Goal name is required"
            );
        }

        return goalRepository
                .findByUser_EmailAndGoalNameContainingIgnoreCase(
                        email,
                        goalName.trim()
                );
    }

    // Find user's goals with deadlines before a specified date
    @Override
    @Transactional(readOnly = true)
    public List<Goal> findByDeadlineBefore(LocalDate deadline) {

        String email = getCurrentUserEmail();

        if (deadline == null) {
            throw new IllegalArgumentException(
                    "Deadline is required"
            );
        }

        return goalRepository.findByUser_EmailAndDeadlineBefore(
                email,
                deadline
        );
    }
}