package com.unnati.fintrack.services;

import java.util.List;

import com.unnati.fintrack.entity.Account;

public interface AccountService {

    List<Account> findAll();

    Account findById(Long id);

    Account save(Account account);

    Account update(Long id, Account account);

    void deleteById(Long id);
}