package com.unnati.fintrack.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.unnati.fintrack.entity.Account;
import com.unnati.fintrack.enums.AccountType;
import com.unnati.fintrack.enums.AccountStatus;
import com.unnati.fintrack.enums.CurrencyType;

public interface AccountRepository extends JpaRepository<Account, Long>
{
	List<Account> findByAccountType(AccountType accountType);

    List<Account> findByStatus(AccountStatus status);

    List<Account> findByCurrency(CurrencyType currency);

    List<Account> findByAccountNameContainingIgnoreCase(String accountName);

}
