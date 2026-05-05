package com.unnati.fintrack.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Transaction {
    private Long id;
    private String title;
    private String description;
    private BigDecimal amount;
    private TransactionType type;
    private PaymentMode paymentMode;
    private LocalDate transactionDate;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Account account;

    @ManyToOne
    private User user;
    //comment to check github updates.
}