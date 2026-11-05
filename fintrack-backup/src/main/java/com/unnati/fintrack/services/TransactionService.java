package com.unnati.fintrack.services;

import java.util.List;

import com.unnati.fintrack.entity.Transaction;

public interface TransactionService {

    List<Transaction> findAll();

    Transaction findById(Long id);

    Transaction save(Transaction transaction);

    Transaction update(Long id, Transaction transaction);

    void deleteById(Long id);
}