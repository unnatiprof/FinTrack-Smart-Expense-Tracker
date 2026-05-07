package com.unnati.fintrack.entity;  // package

import java.math.BigDecimal;  // for money precision
import java.time.LocalDate;  // best for local date

import com.unnati.fintrack.enums.PaymentMode;  // imported payment mode enum.
import com.unnati.fintrack.enums.TransactionStatus;  // imported transaction status enum.
import com.unnati.fintrack.enums.TransactionType;  // imported transaction type enum.

import jakarta.persistence.Entity;  // for importing @entity annotation.
import jakarta.persistence.EnumType;  // used for how to save an enum in a DB.
import jakarta.persistence.Enumerated;  // used to import @Enumerated annotation.
import jakarta.persistence.GeneratedValue; // for importing @GeneratedValue annotation.
import jakarta.persistence.GenerationType; // used to tell the generation strategy for id.
import jakarta.persistence.Id; // for importing @Id annotation.
import jakarta.persistence.Table;  // for importing @Table annotation.
import lombok.AllArgsConstructor;  //  for importing @AllArgsContructor.
import lombok.Builder;  // for importing @Builder.
import lombok.Getter;  // for importing @Getter
import lombok.NoArgsConstructor;  //  for importing @NoArgsContructor.
import lombok.Setter;  //for importing @Setter.

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String description;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String category;

    private String account;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private LocalDate transactionDate;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    private String notes;
}