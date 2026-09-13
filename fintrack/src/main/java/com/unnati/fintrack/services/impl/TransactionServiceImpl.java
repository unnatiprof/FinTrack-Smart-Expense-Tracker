package com.unnati.fintrack.services.impl;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.unnati.fintrack.entity.Transaction;
import com.unnati.fintrack.events.TransactionCreatedEvent;
import com.unnati.fintrack.exception.ResourceNotFoundException;
import com.unnati.fintrack.repository.TransactionRepository;
import com.unnati.fintrack.services.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final ApplicationEventPublisher eventPublisher;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            ApplicationEventPublisher eventPublisher) {

        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction findById(Long id) {

        return transactionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found with id: " + id
                        ));
    }

    @Override
    public Transaction save(Transaction transaction) {

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        eventPublisher.publishEvent(
                new TransactionCreatedEvent(
                        savedTransaction.getId(),
                        savedTransaction.getTitle(),
                        savedTransaction.getAmount(),
                        savedTransaction.getType(),
                        savedTransaction.getCategory()
                )
        );

        return savedTransaction;
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