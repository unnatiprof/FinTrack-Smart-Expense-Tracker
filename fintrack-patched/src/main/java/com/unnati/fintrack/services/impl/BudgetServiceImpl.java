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

import com.unnati.fintrack.entity.Budget;
import com.unnati.fintrack.entity.User;
import com.unnati.fintrack.enums.BudgetStatus;
import com.unnati.fintrack.events.BudgetExceededEvent;
import com.unnati.fintrack.exception.ResourceNotFoundException;
import com.unnati.fintrack.repository.BudgetRepository;
import com.unnati.fintrack.services.BudgetService;
import com.unnati.fintrack.services.UserService;

@Service
@Transactional
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;

    public BudgetServiceImpl(
            BudgetRepository budgetRepository,
            ApplicationEventPublisher eventPublisher,
            UserService userService) {

        this.budgetRepository = budgetRepository;
        this.eventPublisher = eventPublisher;
        this.userService = userService;
    }

    // Get authenticated user's email
    private String getCurrentUserEmail() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            throw new AccessDeniedException("User not authenticated");
        }

        return authentication.getName();
    }

    // Find a budget only if it belongs to the authenticated user
    private Budget findOwnedBudget(Long id, String email) {

        return budgetRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Budget not found with id: " + id
                        ));
    }

    // Validate budget details
    private void validateBudget(Budget budget) {

        if (budget.getBudgetName() == null
                || budget.getBudgetName().isBlank()) {

            throw new IllegalArgumentException(
                    "Budget name is required"
            );
        }

        if (budget.getCategory() == null
                || budget.getCategory().isBlank()) {

            throw new IllegalArgumentException(
                    "Budget category is required"
            );
        }

        if (budget.getLimitAmount() == null
                || budget.getLimitAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Budget limit must be greater than zero"
            );
        }

        if (budget.getMonth() != null
                && (budget.getMonth() < 1 || budget.getMonth() > 12)) {

            throw new IllegalArgumentException(
                    "Month must be between 1 and 12"
            );
        }

        if (budget.getYear() != null && budget.getYear() < 1) {

            throw new IllegalArgumentException(
                    "Year must be a valid positive number"
            );
        }

        if (budget.getStartDate() != null
                && budget.getEndDate() != null
                && budget.getEndDate().isBefore(budget.getStartDate())) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }
    }

    // Publish budget exceeded event
    private void publishBudgetExceededEvent(
            Budget budget,
            User user) {

        if (budget.getStatus() == BudgetStatus.EXCEEDED) {

            eventPublisher.publishEvent(
                    new BudgetExceededEvent(
                            budget.getId(),
                            budget.getBudgetName(),
                            budget.getLimitAmount(),
                            user.getId(),
                            user.getName(),
                            user.getEmail()
                    )
            );
        }
    }

    // Get all budgets of authenticated user
    @Override
    @Transactional(readOnly = true)
    public List<Budget> findAll() {

        String email = getCurrentUserEmail();

        return budgetRepository.findByUser_Email(email);
    }

    // Get budget by ID, only if it belongs to authenticated user
    @Override
    @Transactional(readOnly = true)
    public Budget findById(Long id) {

        String email = getCurrentUserEmail();

        return findOwnedBudget(id, email);
    }

    // Create a budget for authenticated user
    @Override
    public Budget save(Budget budget) {

        String email = getCurrentUserEmail();

        User user = userService.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        ));

        validateBudget(budget);

        // Ignore any ID supplied by the client
        budget.setId(null);

        // Assign owner from authentication, not request body
        budget.setUser(user);

        Budget savedBudget = budgetRepository.save(budget);

        publishBudgetExceededEvent(savedBudget, user);

        return savedBudget;
    }

    // Update only the authenticated user's budget
    @Override
    public Budget update(Long id, Budget budget) {

        String email = getCurrentUserEmail();

        Budget existingBudget = findOwnedBudget(id, email);

        validateBudget(budget);

        existingBudget.setBudgetName(budget.getBudgetName().trim());
        existingBudget.setCategory(budget.getCategory().trim());
        existingBudget.setMonth(budget.getMonth());
        existingBudget.setYear(budget.getYear());
        existingBudget.setLimitAmount(budget.getLimitAmount());
        existingBudget.setStartDate(budget.getStartDate());
        existingBudget.setEndDate(budget.getEndDate());
        existingBudget.setStatus(budget.getStatus());

        Budget updatedBudget = budgetRepository.save(existingBudget);

        publishBudgetExceededEvent(
                updatedBudget,
                existingBudget.getUser()
        );

        return updatedBudget;
    }

    // Delete only the authenticated user's budget
    @Override
    public void deleteById(Long id) {

        String email = getCurrentUserEmail();

        Budget existingBudget = findOwnedBudget(id, email);

        budgetRepository.delete(existingBudget);
    }

    // Find user's budgets by category
    @Override
    @Transactional(readOnly = true)
    public List<Budget> findByCategory(String category) {

        String email = getCurrentUserEmail();

        return budgetRepository.findByUser_EmailAndCategoryIgnoreCase(
                email,
                category
        );
    }

    // Find user's budgets by month
    @Override
    @Transactional(readOnly = true)
    public List<Budget> findByMonth(Integer month) {

        String email = getCurrentUserEmail();

        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "Month must be between 1 and 12"
            );
        }

        return budgetRepository.findByUser_EmailAndMonth(
                email,
                month
        );
    }

    // Find user's budgets by year
    @Override
    @Transactional(readOnly = true)
    public List<Budget> findByYear(Integer year) {

        String email = getCurrentUserEmail();

        if (year == null || year < 1) {
            throw new IllegalArgumentException(
                    "Year must be a valid positive number"
            );
        }

        return budgetRepository.findByUser_EmailAndYear(
                email,
                year
        );
    }

    // Find user's budgets by month and year
    @Override
    @Transactional(readOnly = true)
    public List<Budget> findByMonthAndYear(
            Integer month,
            Integer year) {

        String email = getCurrentUserEmail();

        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "Month must be between 1 and 12"
            );
        }

        if (year == null || year < 1) {
            throw new IllegalArgumentException(
                    "Year must be a valid positive number"
            );
        }

        return budgetRepository.findByUser_EmailAndMonthAndYear(
                email,
                month,
                year
        );
    }

    // Find user's budgets by status
    @Override
    @Transactional(readOnly = true)
    public List<Budget> findByStatus(BudgetStatus status) {

        String email = getCurrentUserEmail();

        if (status == null) {
            throw new IllegalArgumentException(
                    "Budget status is required"
            );
        }

        return budgetRepository.findByUser_EmailAndStatus(
                email,
                status
        );
    }

    // Find user's budgets by date range
    @Override
    @Transactional(readOnly = true)
    public List<Budget> findByStartDateBetween(
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

        return budgetRepository.findByUser_EmailAndStartDateBetween(
                email,
                startDate,
                endDate
        );
    }
}