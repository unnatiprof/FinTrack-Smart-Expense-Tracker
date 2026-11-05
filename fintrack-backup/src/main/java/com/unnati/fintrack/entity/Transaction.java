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
@NoArgsConstructor //  generates empty constructor.
@AllArgsConstructor  // generates all field constructor.
@Builder  // used to generate builder pattern for clean object creation.
@Entity  // means this class is a DB entity.
@Table(name = "transactions") // explicitly gives the name "transactions" to the table created, otherwise by default table name will be the class name.
public class Transaction {

    @Id // means it will be the primary key of the table.
    @GeneratedValue(strategy = GenerationType.AUTO) // This tells JPA to auto-generate the ID. You do not send id in POST request.
    
    private Long id;

    private String title;

    private String description;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING) // This is used above enum fields. It tells JPA: Store enum value as readable string in database.
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