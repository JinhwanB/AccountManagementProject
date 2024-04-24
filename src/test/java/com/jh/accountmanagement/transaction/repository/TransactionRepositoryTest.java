package com.jh.accountmanagement.transaction.repository;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.exception.NotFoundAccountException;
import com.jh.accountmanagement.account.repository.AccountRepository;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import com.jh.accountmanagement.account.type.AccountErrorCode;
import com.jh.accountmanagement.transaction.domain.Transaction;
import com.jh.accountmanagement.transaction.type.TransactionResult;
import com.jh.accountmanagement.transaction.type.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryTest {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountUserRepository accountUserRepository;

    @Autowired
    private AccountRepository accountRepository;

    private AccountUser accountUser;
    private Account account;

    @BeforeEach
    void before() {
        AccountUser accountUserBuild = AccountUser.builder()
                .userId("test")
                .build();
        accountUserBuild.setRegDate(LocalDateTime.now());
        accountUserBuild.setChgDate(LocalDateTime.now());
        accountUser = accountUserRepository.save(accountUserBuild);

        Account accountBuild = Account.builder()
                .accountNum(3204965758L)
                .accountUser(accountUser)
                .money(3000)
                .build();
        accountBuild.setRegDate(LocalDateTime.now());
        accountBuild.setChgDate(LocalDateTime.now());
        account = accountRepository.save(accountBuild);
    }

    @Test
    @DisplayName("잔액 사용")
    void transactionUse() {
        long price = 1000;
        Account useAccount = account.toBuilder()
                .money(account.getMoney() - price)
                .build();
        useAccount.setRegDate(account.getRegDate());
        useAccount.setChgDate(LocalDateTime.now());

        Transaction transactionBuild = Transaction.builder()
                .account(useAccount)
                .accountUser(accountUser)
                .transactionType(TransactionType.TRANSACTION)
                .transactionResult(TransactionResult.S)
                .price(price)
                .build();
        transactionBuild.setRegDate(LocalDateTime.now());
        transactionBuild.setChgDate(LocalDateTime.now());
        Transaction transaction = transactionRepository.save(transactionBuild);
        accountRepository.save(useAccount);

        Account foundAccount = accountRepository.findByAccountNum(3204965758L).orElseThrow(() -> new NotFoundAccountException(AccountErrorCode.NOT_FOUND_ACCOUNT.getMessage()));
        assertThat(transaction.getPrice()).isEqualTo(1000);
        assertThat(transaction.getAccount().getMoney()).isEqualTo(2000);
        assertThat(foundAccount.getMoney()).isEqualTo(2000);
    }
}