package com.jh.accountmanagement;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.repository.AccountRepository;
import com.jh.accountmanagement.account.service.AccountService;
import com.jh.accountmanagement.account.service.AccountUserService;
import com.jh.accountmanagement.transaction.domain.Transaction;
import com.jh.accountmanagement.transaction.dto.TransactionCancelDto;
import com.jh.accountmanagement.transaction.repository.TransactionRepository;
import com.jh.accountmanagement.transaction.service.TransactionService;
import com.jh.accountmanagement.transaction.type.TransactionResult;
import com.jh.accountmanagement.transaction.type.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SameTimeJoin {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountUserService accountUserService;

    @Autowired
    private AccountRepository accountRepository;

    private AccountUser accountUser;
    private Account account;

    @BeforeEach
    void before() {
        accountUser = accountUserService.getUser("test");
        account = Account.builder()
                .accountNum("12345")
                .accountUser(accountUser)
                .money(50000)
                .build();
        account.setRegDate(LocalDateTime.now());
        account.setChgDate(LocalDateTime.now());
        accountRepository.save(account);
    }

    // 동시성 처리 전
    @Test
    @DisplayName("동시성 이슈 테스트 - 100번 -1원 처리 시 결과 확인")
    void sameTime() throws InterruptedException {
        final int threadCount = 100;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        int transactionNumber = 1000;

        for (int i = 0; i < threadCount; i++) {
            Transaction transaction = Transaction.builder()
                    .price(100)
                    .transactionResult(TransactionResult.S)
                    .transactionNumber(String.valueOf(transactionNumber))
                    .transactionType(TransactionType.TRANSACTION)
                    .accountUser(accountUser)
                    .account(account)
                    .build();
            transactionRepository.save(transaction);

            TransactionCancelDto.Request request = TransactionCancelDto.Request.builder()
                    .transactionNumber(String.valueOf(transactionNumber))
                    .accountNum("12345")
                    .price(100)
                    .build();

            final int turn = i;
            executorService.submit(() -> {
                try {
                    transactionService.canceledTransaction(request);
                    Account account1 = accountService.getAccount("12345");
                    System.out.println((turn + 1) + "번쨰 변경 잔액 = " + account1.getMoney());
                } finally {
                    countDownLatch.countDown();
                }
            });
            transactionNumber++;
        }
        countDownLatch.await();

        Account testAccount = accountService.getAccount("12345");

        assertThat(testAccount.getMoney()).isEqualTo(40000);
    }
}
