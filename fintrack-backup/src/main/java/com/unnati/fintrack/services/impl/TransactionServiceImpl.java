package com.unnati.fintrack.services.impl;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.unnati.fintrack.entity.Transaction;
import com.unnati.fintrack.entity.User;
import com.unnati.fintrack.events.TransactionCreatedEvent;
import com.unnati.fintrack.exception.ResourceNotFoundException;
import com.unnati.fintrack.repository.TransactionRepository;
import com.unnati.fintrack.services.TransactionService;
import com.unnati.fintrack.services.UserService;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final UserService userService;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            ApplicationEventPublisher eventPublisher,
            UserService userService) {

        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
        this.userService = userService;
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

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User user = userService.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        ));

        eventPublisher.publishEvent(
                new TransactionCreatedEvent(
                        savedTransaction.getId(),
                        savedTransaction.getTitle(),
                        savedTransaction.getAmount(),
                        savedTransaction.getType(),
                        savedTransaction.getCategory(),
                        user.getId(),
                        user.getName(),
                        user.getEmail()
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