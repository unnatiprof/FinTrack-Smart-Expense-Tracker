package com.unnati.fintrack.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.unnati.fintrack.entity.Budget;
import com.unnati.fintrack.entity.User;
import com.unnati.fintrack.enums.BudgetStatus;
import com.unnati.fintrack.events.BudgetExceededEvent;
import com.unnati.fintrack.exception.ResourceNotFoundException;
import com.unnati.fintrack.repository.BudgetRepository;
import com.unnati.fintrack.services.BudgetService;
import com.unnati.fintrack.services.UserService;

@Service
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

    @Override
    public List<Budget> findAll() {

        return budgetRepository.findAll();
    }

    @Override
    public Budget findById(Long id) {

        return budgetRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Budget not found with id: " + id
                        ));
    }

    @Override
    public Budget save(Budget budget) {

        Budget savedBudget =
                budgetRepository.save(budget);

        if (savedBudget.getStatus() == BudgetStatus.EXCEEDED) {

            Authentication authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with email: " + email
                            ));

            eventPublisher.publishEvent(
                    new BudgetExceededEvent(
                            savedBudget.getId(),
                            savedBudget.getBudgetName(),
                            savedBudget.getLimitAmount(),
                            user.getId(),
                            user.getName(),
                            user.getEmail()
                    )
            );
        }

        return savedBudget;
    }

    @Override
    public Budget update(Long id, Budget budget) {

        Budget existingBudget = findById(id);

        existingBudget.setBudgetName(budget.getBudgetName());
        existingBudget.setCategory(budget.getCategory());
        existingBudget.setMonth(budget.getMonth());
        existingBudget.setYear(budget.getYear());
        existingBudget.setLimitAmount(budget.getLimitAmount());
        existingBudget.setStartDate(budget.getStartDate());
        existingBudget.setEndDate(budget.getEndDate());
        existingBudget.setStatus(budget.getStatus());

        Budget updatedBudget =
                budgetRepository.save(existingBudget);

        if (updatedBudget.getStatus() == BudgetStatus.EXCEEDED) {

            Authentication authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with email: " + email
                            ));

            eventPublisher.publishEvent(
                    new BudgetExceededEvent(
                            updatedBudget.getId(),
                            updatedBudget.getBudgetName(),
                            updatedBudget.getLimitAmount(),
                            user.getId(),
                            user.getName(),
                            user.getEmail()
                    )
            );
        }

        return updatedBudget;
    }

    @Override
    public void deleteById(Long id) {

        Budget existingBudget = findById(id);

        budgetRepository.delete(existingBudget);
    }

    @Override
    public List<Budget> findByCategory(String category) {

        return budgetRepository.findByCategory(category);
    }

    @Override
    public List<Budget> findByMonth(Integer month) {

        return budgetRepository.findByMonth(month);
    }

    @Override
    public List<Budget> findByYear(Integer year) {

        return budgetRepository.findByYear(year);
    }

    @Override
    public List<Budget> findByMonthAndYear(
            Integer month,
            Integer year) {

        return budgetRepository.findByMonthAndYear(month, year);
    }

    @Override
    public List<Budget> findByStatus(BudgetStatus status) {

        return budgetRepository.findByStatus(status);
    }

    @Override
    public List<Budget> findByStartDateBetween(
            LocalDate startDate,
            LocalDate endDate) {

        return budgetRepository.findByStartDateBetween(
                startDate,
                endDate
        );
    }
}
//new file changes