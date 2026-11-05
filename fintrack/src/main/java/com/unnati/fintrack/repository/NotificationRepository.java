package com.unnati.fintrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unnati.fintrack.entity.Notification;
import com.unnati.fintrack.enums.NotificationStatus;
import com.unnati.fintrack.enums.NotificationType;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUser_EmailOrderByCreatedAtDesc(String email);

    Optional<Notification> findByIdAndUser_Email(
            Long id, String email);

    List<Notification> findByUser_EmailAndStatusOrderByCreatedAtDesc(
            String email, NotificationStatus status);

    List<Notification> findByUser_EmailAndTypeOrderByCreatedAtDesc(
            String email, NotificationType type);

    List<Notification> findByUser_EmailAndStatus(
            String email, NotificationStatus status);

    long countByUser_EmailAndStatus(
            String email, NotificationStatus status);
}