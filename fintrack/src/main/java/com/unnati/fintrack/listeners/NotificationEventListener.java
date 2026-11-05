
package com.unnati.fintrack.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.unnati.fintrack.enums.NotificationType;
import com.unnati.fintrack.events.BudgetExceededEvent;
import com.unnati.fintrack.events.GoalCompletedEvent;
import com.unnati.fintrack.events.TransactionCreatedEvent;
import com.unnati.fintrack.events.UserLoggedInEvent;
import com.unnati.fintrack.events.UserRegisteredEvent;
import com.unnati.fintrack.services.AuditLogService;
import com.unnati.fintrack.services.NotificationService;

@Component
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public NotificationEventListener(
            NotificationService notificationService,
            AuditLogService auditLogService) {

        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    // TRANSACTION CREATED: Notification + AuditLog
    @EventListener
    public void handleTransactionCreated(TransactionCreatedEvent event) {

        notificationService.createForUser(
                event.getUserId(),
                "Transaction Added",
                event.getType() + " transaction of ₹"
                        + event.getAmount()
                        + " was added successfully.",
                NotificationType.SYSTEM,
                "View Transaction",
                "/api/transactions/" + event.getTransactionId()
        );

        auditLogService.save(
                com.unnati.fintrack.entity.AuditLog.builder()
                        .userId(event.getUserId())
                        .userName(event.getUserName())
                        .userEmail(event.getUserEmail())
                        .action("TRANSACTION_CREATED")
                        .description(
                                "Transaction created: "
                                        + event.getTitle()
                                        + " of ₹"
                                        + event.getAmount())
                        .build()
        );
    }

    // BUDGET EXCEEDED: Notification + AuditLog
    @EventListener
    public void handleBudgetExceeded(BudgetExceededEvent event) {

        notificationService.createForUser(
                event.getUserId(),
                "Budget Exceeded",
                "Your " + event.getBudgetName()
                        + " budget has exceeded its limit of ₹"
                        + event.getLimitAmount() + ".",
                NotificationType.BUDGET,
                "Review Budget",
                "/api/budgets/" + event.getBudgetId()
        );

        auditLogService.save(
                com.unnati.fintrack.entity.AuditLog.builder()
                        .userId(event.getUserId())
                        .userName(event.getUserName())
                        .userEmail(event.getUserEmail())
                        .action("BUDGET_EXCEEDED")
                        .description(
                                "Budget exceeded: "
                                        + event.getBudgetName()
                                        + " with limit of ₹"
                                        + event.getLimitAmount())
                        .build()
        );
    }

    // GOAL COMPLETED: Notification + AuditLog
    @EventListener
    public void handleGoalCompleted(GoalCompletedEvent event) {

        notificationService.createForUser(
                event.getUserId(),
                "Goal Completed",
                "Congratulations! You have completed your "
                        + event.getGoalName()
                        + " goal of ₹"
                        + event.getTargetAmount() + ".",
                NotificationType.GOAL,
                "View Goal",
                "/api/goals/" + event.getGoalId()
        );

        auditLogService.save(
                com.unnati.fintrack.entity.AuditLog.builder()
                        .userId(event.getUserId())
                        .userName(event.getUserName())
                        .userEmail(event.getUserEmail())
                        .action("GOAL_COMPLETED")
                        .description(
                                "Goal completed: "
                                        + event.getGoalName()
                                        + " with target amount of ₹"
                                        + event.getTargetAmount())
                        .build()
        );
    }

    // USER REGISTERED: Notification + AuditLog
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {

        notificationService.createForUser(
                event.getUserId(),
                "Welcome to FinTrack",
                "Welcome " + event.getName()
                        + "! Your FinTrack account has been created successfully.",
                NotificationType.SYSTEM,
                "View Profile",
                "/api/users/" + event.getUserId()
        );

        auditLogService.save(
                com.unnati.fintrack.entity.AuditLog.builder()
                        .userId(event.getUserId())
                        .userName(event.getName())
                        .userEmail(event.getEmail())
                        .action("REGISTER")
                        .description("User registered successfully")
                        .build()
        );
    }

    // USER LOGIN: Notification + AuditLog
    @EventListener
    public void handleUserLoggedIn(UserLoggedInEvent event) {

        notificationService.createForUser(
                event.getUserId(),
                "Login Successful",
                "Welcome back, " + event.getName()
                        + "! You logged into FinTrack successfully.",
                NotificationType.SECURITY,
                "View Profile",
                "/api/users/profile"
        );

        auditLogService.save(
                com.unnati.fintrack.entity.AuditLog.builder()
                        .userId(event.getUserId())
                        .userName(event.getName())
                        .userEmail(event.getEmail())
                        .action("LOGIN")
                        .description("User logged into FinTrack successfully")
                        .build()
        );
    }
}
