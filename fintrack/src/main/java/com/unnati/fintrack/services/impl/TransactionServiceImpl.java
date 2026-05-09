package com.unnati.fintrack.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.unnati.fintrack.entity.Transaction;
import com.unnati.fintrack.repository.TransactionRepository;
import com.unnati.fintrack.services.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction update(Long id, Transaction transaction) {
        Transaction existingTransaction = findById(id);

        existingTransaction.setTitle(transaction.getTitle());
        existingTransaction.setDescription(transaction.getDescription());
        existingTransaction.setAmount(transaction.getAmount());
        existingTransaction.setType(transaction.getType());
        existingTransaction.setCategory(transaction.getCategory());
        existingTransaction.setAccount(transaction.getAccount());
        existingTransaction.setPaymentMode(transaction.getPaymentMode());
        existingTransaction.setTransactionDate(transaction.getTransactionDate());
        existingTransaction.setTransactionStatus(transaction.getTransactionStatus());
        existingTransaction.setNotes(transaction.getNotes());

        return transactionRepository.save(existingTransaction);
    }

    @Override
    public void deleteById(Long id) {
        Transaction existingTransaction = findById(id);
        transactionRepository.delete(existingTransaction);
    }
}