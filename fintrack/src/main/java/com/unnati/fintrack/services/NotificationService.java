package com.unnati.fintrack.services;

import java.util.List;

import com.unnati.fintrack.entity.Notification;
import com.unnati.fintrack.enums.NotificationStatus;
import com.unnati.fintrack.enums.NotificationType;

public interface NotificationService {

    List<Notification> findAll();

    Notification findById(Long id);

    Notification save(Notification notification);

    void deleteById(Long id);

    Notification markAsRead(Long id);

    void markAllAsRead();

    List<Notification> findByStatus(
            NotificationStatus status
    );

    List<Notification> findByType(
            NotificationType type
    );
}