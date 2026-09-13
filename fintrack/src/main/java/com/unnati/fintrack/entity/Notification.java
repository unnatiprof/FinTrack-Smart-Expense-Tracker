package com.unnati.fintrack.entity;

import java.time.LocalDateTime;

import com.unnati.fintrack.enums.NotificationStatus;
import com.unnati.fintrack.enums.NotificationType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private LocalDateTime createdAt;

    private String actionLabel;

    private String actionUrl;

    @PrePersist
    public void beforeSave() {

        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = NotificationStatus.UNREAD;
        }
    }
}