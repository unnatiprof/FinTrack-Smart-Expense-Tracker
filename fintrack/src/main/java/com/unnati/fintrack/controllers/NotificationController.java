package com.unnati.fintrack.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unnati.fintrack.entity.Notification;
import com.unnati.fintrack.enums.NotificationStatus;
import com.unnati.fintrack.enums.NotificationType;
import com.unnati.fintrack.services.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService =
                notificationService;
    }

    // =========================================================
    // GET ALL NOTIFICATIONS
    // GET /api/notifications
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Notification>>
    getAllNotifications() {

        return ResponseEntity.ok(
                notificationService.findAll()
        );
    }

    // =========================================================
    // GET NOTIFICATION BY ID
    // GET /api/notifications/{id}
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<Notification>
    getNotificationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                notificationService.findById(id)
        );
    }

    // =========================================================
    // CREATE NOTIFICATION
    // POST /api/notifications
    // =========================================================

    @PostMapping
    public ResponseEntity<Notification>
    createNotification(
            @RequestBody Notification notification) {

        Notification savedNotification =
                notificationService.save(notification);

        URI location = URI.create(
                "/api/notifications/"
                        + savedNotification.getId()
        );

        return ResponseEntity
                .created(location)
                .body(savedNotification);
    }

    // =========================================================
    // MARK AS READ
    // PATCH /api/notifications/{id}/read
    // =========================================================

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification>
    markAsRead(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                notificationService.markAsRead(id)
        );
    }

    // =========================================================
    // MARK ALL AS READ
    // PATCH /api/notifications/read-all
    // =========================================================

    @PatchMapping("/read-all")
    public ResponseEntity<Void>
    markAllAsRead() {

        notificationService.markAllAsRead();

        return ResponseEntity
                .noContent()
                .build();
    }

    // =========================================================
    // DELETE
    // DELETE /api/notifications/{id}
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteNotification(
            @PathVariable Long id) {

        notificationService.deleteById(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    // =========================================================
    // FILTER BY STATUS
    // GET /api/notifications/status/UNREAD
    // =========================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Notification>>
    getNotificationsByStatus(
            @PathVariable NotificationStatus status) {

        return ResponseEntity.ok(
                notificationService.findByStatus(status)
        );
    }

    // =========================================================
    // FILTER BY TYPE
    // GET /api/notifications/type/BUDGET
    // =========================================================

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Notification>>
    getNotificationsByType(
            @PathVariable NotificationType type) {

        return ResponseEntity.ok(
                notificationService.findByType(type)
        );
    }
}