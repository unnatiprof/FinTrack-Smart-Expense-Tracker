package com.unnati.fintrack.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unnati.fintrack.entity.Transaction;
import com.unnati.fintrack.enums.PaymentMode;
import com.unnati.fintrack.enums.TransactionStatus;
import com.unnati.fintrack.enums.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByCategory(String category);

    List<Transaction> findByAccount(String account);

    List<Transaction> findByPaymentMode(PaymentMode paymentMode);

    List<Transaction> findByTransactionStatus(TransactionStatus transactionStatus);

    List<Transaction> findByTransactionDate(LocalDate transactionDate);

    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
}