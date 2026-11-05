package com.unnati.fintrack.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unnati.fintrack.enums.GoalPriority;
import com.unnati.fintrack.enums.GoalStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String goalName;

    private BigDecimal targetAmount;

    private BigDecimal savedAmount;

    private LocalDate deadline;

    private String icon;

    @Enumerated(EnumType.STRING)
    private GoalPriority priority;

    @Enumerated(EnumType.STRING)
    private GoalStatus status;

    @Transient
    public BigDecimal getRemainingAmount() {

        if (targetAmount == null || savedAmount == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal remaining = targetAmount.subtract(savedAmount);

        return remaining.max(BigDecimal.ZERO);
    }

    @Transient
    public BigDecimal getProgressPercentage() {

        if (targetAmount == null
                || savedAmount == null
                || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {

            return BigDecimal.ZERO;
        }

        return savedAmount
                .multiply(BigDecimal.valueOf(100))
                .divide(targetAmount, 2, RoundingMode.HALF_UP)
                .min(BigDecimal.valueOf(100));
    }
}