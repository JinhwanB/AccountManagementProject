package com.jh.accountmanagement;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.dto.AccountRedisDto;
import com.jh.accountmanagement.config.RedisUtils;
import com.jh.accountmanagement.transaction.domain.Transaction;
import com.jh.accountmanagement.transaction.dto.TransactionRedisDto;
import com.jh.accountmanagement.transaction.type.TransactionResult;
import com.jh.accountmanagement.transaction.type.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisUtils redisUtils;

    private AccountUser accountUser;
    private Account account;

    @BeforeEach
    void before() {
        accountUser = AccountUser.builder()
                .userId("best")
                .build();
        accountUser.setRegDate(LocalDateTime.now());
        accountUser.setChgDate(LocalDateTime.now());
        account = Account.builder()
                .money(3000)
                .accountUser(accountUser)
                .accountNum("12345")
                .build();
        account.setRegDate(LocalDateTime.now());
        account.setChgDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("계좌 redis 테스트")
    void accountRedis() {
        Account account2 = Account.builder()
                .accountNum("11111")
                .accountUser(accountUser)
                .money(3000)
                .build();
        redisUtils.setAccount("best", account.toRedisDto());
        redisUtils.setAccount("best", account2.toRedisDto());

        List<AccountRedisDto> accounts = redisUtils.getAccount("best");

        assertThat(redisUtils.hasKeyOfAccount("best")).isEqualTo(true);
        assertThat(accounts).hasSize(2);
        assertThat(accounts.get(0).getAccountNum()).isEqualTo("12345");

        redisUtils.deleteAccount("best");
    }

    @Test
    @DisplayName("계좌 redis 삭제 테스트")
    void accountRedisDelete() {
        Account account2 = Account.builder()
                .accountNum("11111")
                .accountUser(accountUser)
                .money(3000)
                .build();
        redisUtils.setAccount("best", account.toRedisDto());
        redisUtils.setAccount("best", account2.toRedisDto());

        redisUtils.deleteAccount("best");

        assertThat(redisUtils.hasKeyOfAccount("best")).isEqualTo(false);
    }

    @Test
    @DisplayName("계좌 redis 자동 삭제 테스트")
    void accountRedisDeleteAuto() throws InterruptedException {
        Account account2 = Account.builder()
                .accountNum("11111")
                .accountUser(accountUser)
                .money(3000)
                .build();
        redisUtils.setAccount("best", account.toRedisDto());
        redisUtils.setAccount("best", account2.toRedisDto());

        Thread.sleep(10000);

        assertThat(redisUtils.hasKeyOfAccount("best")).isEqualTo(false);
    }

    @Test
    @DisplayName("거래 redis 테스트")
    void redisTransaction() {
        Transaction transaction = Transaction.builder()
                .price(1000)
                .accountUser(accountUser)
                .transactionResult(TransactionResult.S)
                .transactionType(TransactionType.TRANSACTION)
                .transactionNumber("34567")
                .account(account)
                .build();
        transaction.setRegDate(LocalDateTime.now());
        transaction.setChgDate(LocalDateTime.now());

        redisUtils.setTransaction("34567", transaction.toRedisDto());

        assertThat(redisUtils.hasKeyOfTransaction("34567")).isEqualTo(true);

        TransactionRedisDto transactionRedisDto = redisUtils.getTransaction("34567");

        assertThat(transactionRedisDto.getTransactionNumber()).isEqualTo("34567");
        assertThat(transactionRedisDto.getTransactionType()).isEqualTo(TransactionType.TRANSACTION.getMessage());

        redisUtils.deleteTransaction("34567");
    }

    @Test
    @DisplayName("거래 redis 업데이트 테스트")
    void transactionRedisUpdate() {
        Transaction transaction = Transaction.builder()
                .price(1000)
                .accountUser(accountUser)
                .transactionResult(TransactionResult.S)
                .transactionType(TransactionType.TRANSACTION)
                .transactionNumber("34567")
                .account(account)
                .build();
        transaction.setRegDate(LocalDateTime.now());
        transaction.setChgDate(LocalDateTime.now());
        Transaction modified = transaction.toBuilder()
                .price(4000)
                .build();

        redisUtils.setTransaction("34567", transaction.toRedisDto());
        redisUtils.updateTransaction("34567", modified.toRedisDto());

        assertThat(redisUtils.getTransaction("34567").getPrice()).isEqualTo(4000);

        redisUtils.deleteTransaction("34567");
    }

    @Test
    @DisplayName("거래 redis 삭제 테스트")
    void transactionRedisDelete() {
        Transaction transaction = Transaction.builder()
                .price(1000)
                .accountUser(accountUser)
                .transactionResult(TransactionResult.S)
                .transactionType(TransactionType.TRANSACTION)
                .transactionNumber("34567")
                .account(account)
                .build();
        transaction.setRegDate(LocalDateTime.now());
        transaction.setChgDate(LocalDateTime.now());
        redisUtils.setTransaction("34567", transaction.toRedisDto());

        redisUtils.deleteTransaction("34567");

        assertThat(redisUtils.hasKeyOfTransaction("34567")).isEqualTo(false);
    }

    @Test
    @DisplayName("거래 redis 데이터 자동 삭제 테스트")
    void redisAutoDelete() throws InterruptedException {
        Transaction transaction = Transaction.builder()
                .price(1000)
                .accountUser(accountUser)
                .transactionResult(TransactionResult.S)
                .transactionType(TransactionType.TRANSACTION)
                .transactionNumber("34567")
                .account(account)
                .build();
        transaction.setRegDate(LocalDateTime.now());
        transaction.setChgDate(LocalDateTime.now());

        redisUtils.setTransaction("34567", transaction.toRedisDto());
        System.out.println(redisUtils.getTransaction("34567").getTransactionNumber());
        Thread.sleep(10000); // 테스트 상에서는 RedisUtils의 timeout을 5초로 설정하고 진행함

        assertThat(redisUtils.hasKeyOfTransaction("34567")).isEqualTo(false);
    }
}
