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

    private String getCurrentUserEmail() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "User is not authenticated"
            );
        }

        return authentication.getName();
    }

    @Override
    public List<Transaction> findAll() {

        String email = getCurrentUserEmail();

        return transactionRepository.findByUser_Email(email);
    }

    @Override
    public Transaction findById(Long id) {

        String email = getCurrentUserEmail();

        return transactionRepository
                .findByIdAndUser_Email(id, email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found"
                        ));
    }

    @Override
    public Transaction save(Transaction transaction) {
    	
    	if (transaction.getId() != null) {
    	    throw new IllegalArgumentException(
    	        "Transaction ID must not be provided when creating a transaction"
    	    );
    	}

        String email = getCurrentUserEmail();

        User user = userService.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + email
                        ));

        // Assign transaction ownership on the server.
        transaction.setUser(user);

        Transaction savedTransaction =
                transactionRepository.save(transaction);

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

        // Never accept a user relationship from the request body.

        return transactionRepository.save(existingTransaction);
    }

    @Override
    public void deleteById(Long id) {

        Transaction existingTransaction = findById(id);

        transactionRepository.delete(existingTransaction);
    }
    

}