package com.unnati.fintrack.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.unnati.fintrack.enums.AccountStatus;
import com.unnati.fintrack.enums.AccountType;

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
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String accountName;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private BigDecimal currentBalance;

    @Enumerated(EnumType.STRING)
    private CurrencyType currency;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void beforeSave() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = AccountStatus.ACTIVE;
        }

        if (this.currency == null) {
            this.currency = CurrencyType.INR;
        }

        if (this.currentBalance == null) {
            this.currentBalance = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}