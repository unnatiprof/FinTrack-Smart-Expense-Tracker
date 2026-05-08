package com.unnati.fintrack.entity;

import java.time.LocalDateTime;

import com.unnati.fintrack.enums.CategoryStatus;
import com.unnati.fintrack.enums.CategoryType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private CategoryType category;

    private String colorCode;

    private String icon;

    @Enumerated(EnumType.STRING)
    private CategoryStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void beforeSave() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = CategoryStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}