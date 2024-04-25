package com.jh.accountmanagement.transaction.repository;

import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.transaction.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionNumber(String transactionNumber);

    List<Transaction> findByAccountUser(AccountUser accountUser);
}
