package com.unnati.fintrack.services.impl;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.unnati.fintrack.entity.Account;
import com.unnati.fintrack.entity.User;
import com.unnati.fintrack.exception.ResourceNotFoundException;
import com.unnati.fintrack.repository.AccountRepository;
import com.unnati.fintrack.services.AccountService;
import com.unnati.fintrack.services.UserService;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    public AccountServiceImpl(
            AccountRepository accountRepository,
            UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    private String getCurrentUserEmail() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("User not authenticated");
        }

        return authentication.getName();
    }

    @Override
    public List<Account> findAll() {

        String email = getCurrentUserEmail();

        return accountRepository.findByUser_Email(email);
    }

    @Override
    public Account findById(Long id) {

        String email = getCurrentUserEmail();

        return accountRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));
    }

    @Override
    public Account save(Account account) {

        String email = getCurrentUserEmail();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        account.setId(null);
        account.setUser(user);

        return accountRepository.save(account);
    }

    @Override
    public Account update(Long id, Account account) {

        Account existingAccount = findById(id);

        existingAccount.setAccountName(account.getAccountName());
        existingAccount.setAccountType(account.getAccountType());
        existingAccount.setCurrentBalance(account.getCurrentBalance());
        existingAccount.setCurrency(account.getCurrency());
        existingAccount.setStatus(account.getStatus());

        return accountRepository.save(existingAccount);
    }

    @Override
    public void deleteById(Long id) {

        Account existingAccount = findById(id);

        accountRepository.delete(existingAccount);
    }
}