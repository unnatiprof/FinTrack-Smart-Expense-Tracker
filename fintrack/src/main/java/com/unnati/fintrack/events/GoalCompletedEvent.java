package com.unnati.fintrack.events;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoalCompletedEvent {

    private Long goalId;

    private String goalName;

    private BigDecimal targetAmount;

    private BigDecimal savedAmount;
}