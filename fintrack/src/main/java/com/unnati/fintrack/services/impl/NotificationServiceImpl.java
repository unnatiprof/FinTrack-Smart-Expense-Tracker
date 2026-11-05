package com.unnati.fintrack.services.impl;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unnati.fintrack.entity.Notification;
import com.unnati.fintrack.entity.User;
import com.unnati.fintrack.enums.NotificationStatus;
import com.unnati.fintrack.enums.NotificationType;
import com.unnati.fintrack.exception.ResourceNotFoundException;
import com.unnati.fintrack.repository.NotificationRepository;
import com.unnati.fintrack.repository.UserRepository;
import com.unnati.fintrack.services.NotificationService;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {

        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    private String getCurrentUserEmail() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().equals("anonymousUser")) {

            throw new IllegalStateException(
                    "Authenticated user not found");
        }

        return authentication.getName();
    }

    private Notification getOwnedNotification(Long id, String email) {

        return notificationRepository
                .findByIdAndUser_Email(id, email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notification not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findAll() {

        String email = getCurrentUserEmail();

        return notificationRepository
                .findByUser_EmailOrderByCreatedAtDesc(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Notification findById(Long id) {

        String email = getCurrentUserEmail();

        return getOwnedNotification(id, email);
    }

    @Override
    public Notification save(Notification notification) {

        if (notification == null) {
            throw new IllegalArgumentException(
                    "Notification cannot be null");
        }

        String email = getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        // Never trust the user or ID supplied in the request body.
        notification.setId(null);
        notification.setUser(user);
        notification.setStatus(NotificationStatus.UNREAD);

        return notificationRepository.save(notification);
    }

    @Override
    public void deleteById(Long id) {

        String email = getCurrentUserEmail();

        Notification notification =
                getOwnedNotification(id, email);

        notificationRepository.delete(notification);
    }

    @Override
    public Notification markAsRead(Long id) {

        String email = getCurrentUserEmail();

        Notification notification =
                getOwnedNotification(id, email);

        notification.setStatus(NotificationStatus.READ);

        return notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead() {

        String email = getCurrentUserEmail();

        List<Notification> unreadNotifications =
                notificationRepository.findByUser_EmailAndStatus(
                        email, NotificationStatus.UNREAD);

        for (Notification notification : unreadNotifications) {
            notification.setStatus(NotificationStatus.READ);
        }

        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findByStatus(
            NotificationStatus status) {

        String email = getCurrentUserEmail();

        return notificationRepository
                .findByUser_EmailAndStatusOrderByCreatedAtDesc(
                        email, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findByType(
            NotificationType type) {

        String email = getCurrentUserEmail();

        return notificationRepository
                .findByUser_EmailAndTypeOrderByCreatedAtDesc(
                        email, type);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {

        String email = getCurrentUserEmail();

        return notificationRepository.countByUser_EmailAndStatus(
                email, NotificationStatus.UNREAD);
    }

    // Internal method for application events.
    // It uses event.userId, not the HTTP security context.
    @Override
    public Notification createForUser(
            Long userId,
            String title,
            String message,
            NotificationType type,
            String actionLabel,
            String actionUrl) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId));

        Notification notification = new Notification();

        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setActionLabel(actionLabel);
        notification.setActionUrl(actionUrl);
        notification.setUser(user);

        return notificationRepository.save(notification);
    }
}