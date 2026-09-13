package com.unnati.fintrack.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.unnati.fintrack.entity.Notification;
import com.unnati.fintrack.enums.NotificationStatus;
import com.unnati.fintrack.enums.NotificationType;
import com.unnati.fintrack.exception.ResourceNotFoundException;
import com.unnati.fintrack.repository.NotificationRepository;
import com.unnati.fintrack.services.NotificationService;

@Service
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository) {

        this.notificationRepository =
                notificationRepository;
    }

    @Override
    public List<Notification> findAll() {

        return notificationRepository.findAll();
    }

    @Override
    public Notification findById(Long id) {

        return notificationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notification not found with id: " + id
                        )
                );
    }

    @Override
    public Notification save(Notification notification) {

        return notificationRepository.save(notification);
    }

    @Override
    public void deleteById(Long id) {

        Notification existingNotification =
                findById(id);

        notificationRepository.delete(
                existingNotification
        );
    }

    @Override
    public Notification markAsRead(Long id) {

        Notification notification =
                findById(id);

        notification.setStatus(
                NotificationStatus.READ
        );

        return notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead() {

        List<Notification> notifications =
                notificationRepository.findAll();

        for (Notification notification : notifications) {

            notification.setStatus(
                    NotificationStatus.READ
            );
        }

        notificationRepository.saveAll(notifications);
    }

    @Override
    public List<Notification> findByStatus(
            NotificationStatus status) {

        return notificationRepository.findByStatus(status);
    }

    @Override
    public List<Notification> findByType(
            NotificationType type) {

        return notificationRepository.findByType(type);
    }
}