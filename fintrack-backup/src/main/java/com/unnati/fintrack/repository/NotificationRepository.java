package com.unnati.fintrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unnati.fintrack.entity.Notification;
import com.unnati.fintrack.enums.NotificationStatus;
import com.unnati.fintrack.enums.NotificationType;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByStatus(
            NotificationStatus status
    );

    List<Notification> findByType(
            NotificationType type
    );
}