package com.unnati.fintrack.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.unnati.fintrack.entity.AuditLog;
import com.unnati.fintrack.entity.Notification;
import com.unnati.fintrack.enums.NotificationStatus;
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

    @EventListener
    public void handleTransactionCreated(TransactionCreatedEvent event) {

        Notification notification = Notification.builder()
            .title("Transaction Added")
            .message(event.getType() + " transaction of ₹" + event.getAmount() + " was added successfully.")
            .type(NotificationType.SYSTEM)
            .status(NotificationStatus.UNREAD)
            .actionLabel("View Transaction")
            .actionUrl("/api/transactions/" + event.getTransactionId())
            .build();

        notificationService.save(notification);

        AuditLog auditLog = AuditLog.builder()
            .userId(event.getUserId())
            .userName(event.getUserName())
            .userEmail(event.getUserEmail())
            .action("TRANSACTION_CREATED")
            .description(
                "Transaction created: "
                + event.getTitle()
                + " of ₹"
                + event.getAmount()
            )
            .build();

        auditLogService.save(auditLog);
    }

    @EventListener
    public void handleBudgetExceeded(
            BudgetExceededEvent event) {

        Notification notification = Notification.builder()
                .title("Budget Exceeded")
                .message(
                        "Your "
                                + event.getBudgetName()
                                + " budget has exceeded its limit of ₹"
                                + event.getLimitAmount()
                                + ".")
                .type(NotificationType.BUDGET)
                .status(NotificationStatus.UNREAD)
                .actionLabel("Review Budget")
                .actionUrl(
                        "/api/budgets/"
                                + event.getBudgetId())
                .build();

        notificationService.save(notification);
    }

    @EventListener
    public void handleGoalCompleted(
            GoalCompletedEvent event) {

        Notification notification = Notification.builder()
                .title("Goal Completed")
                .message(
                        "Congratulations! You have completed your "
                                + event.getGoalName()
                                + " goal of ₹"
                                + event.getTargetAmount()
                                + ".")
                .type(NotificationType.GOAL)
                .status(NotificationStatus.UNREAD)
                .actionLabel("View Goal")
                .actionUrl(
                        "/api/goals/"
                                + event.getGoalId())
                .build();

        notificationService.save(notification);
    }

    @EventListener
    public void handleUserRegistered(
            UserRegisteredEvent event) {

        // Create notification
        Notification notification = Notification.builder()
                .title("Welcome to FinTrack")
                .message(
                        "Welcome "
                                + event.getName()
                                + "! Your FinTrack account has been created successfully.")
                .type(NotificationType.SYSTEM)
                .status(NotificationStatus.UNREAD)
                .actionLabel("View Profile")
                .actionUrl(
                        "/api/users/"
                                + event.getUserId())
                .build();

        notificationService.save(notification);

        // Create audit log
        AuditLog auditLog = AuditLog.builder()
                .userId(event.getUserId())
                .userName(event.getName())
                .userEmail(event.getEmail())
                .action("REGISTER")
                .description("User registered successfully")
                .build();

        auditLogService.save(auditLog);
    }

    @EventListener
    public void handleUserLoggedIn(
            UserLoggedInEvent event) {

        AuditLog auditLog = AuditLog.builder()
                .userId(event.getUserId())
                .userName(event.getName())
                .userEmail(event.getEmail())
                .action("LOGIN")
                .description("User logged into FinTrack successfully")
                .build();

        auditLogService.save(auditLog);
    }
}