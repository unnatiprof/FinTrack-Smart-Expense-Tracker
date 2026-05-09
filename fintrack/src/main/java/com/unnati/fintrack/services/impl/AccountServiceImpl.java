package com.unnati.fintrack.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.unnati.fintrack.entity.Account;
import com.unnati.fintrack.repository.AccountRepository;
import com.unnati.fintrack.services.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }

    @Override
    public Account save(Account account) {
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