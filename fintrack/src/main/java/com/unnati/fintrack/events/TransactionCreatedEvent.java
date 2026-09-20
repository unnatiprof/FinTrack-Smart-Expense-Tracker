package com.unnati.fintrack.events;

import java.math.BigDecimal;

import com.unnati.fintrack.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionCreatedEvent {

    private Long transactionId;

    private String title;

    private BigDecimal amount;

    private TransactionType type;

    private String category;

    private Long userId;

    private String userName;

    private String userEmail;
}