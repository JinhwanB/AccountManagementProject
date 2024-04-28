package com.jh.accountmanagement.transaction.service;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.repository.AccountRepository;
import com.jh.accountmanagement.account.service.AccountService;
import com.jh.accountmanagement.account.service.AccountUserService;
import com.jh.accountmanagement.config.RedisUtils;
import com.jh.accountmanagement.transaction.domain.Transaction;
import com.jh.accountmanagement.transaction.dto.TransactionCancelDto;
import com.jh.accountmanagement.transaction.dto.TransactionUseDto;
import com.jh.accountmanagement.transaction.exception.TransactionException;
import com.jh.accountmanagement.transaction.repository.TransactionRepository;
import com.jh.accountmanagement.transaction.type.TransactionErrorCode;
import com.jh.accountmanagement.transaction.type.TransactionResult;
import com.jh.accountmanagement.transaction.type.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private AccountUserService accountUserService;

    @Mock
    private RedisUtils redisUtils;

    @InjectMocks
    private TransactionService transactionService;

    private AccountUser accountUser;
    private Account account;

    @BeforeEach
    void before() {
        accountUser = AccountUser.builder()
                .userId("test")
                .build();
        account = Account.builder()
                .accountNum("12345")
                .accountUser(accountUser)
                .money(3000)
                .build();
    }

    @Test
    @DisplayName("잔액 사용 서비스 테스트")
    void accountMoneyUse() {
        TransactionUseDto.Request request = TransactionUseDto.Request.builder()
                .accountNum("3487659102")
                .price(1000)
                .userId("test")
                .build();
        Account modifiedAccount = Account.builder()
                .accountNum("3487659102")
                .accountUser(accountUser)
                .money(2000)
                .build();
        Transaction transactionBuild = Transaction.builder()
                .transactionNumber("32545")
                .transactionType(TransactionType.TRANSACTION)
                .transactionResult(TransactionResult.S)
                .accountUser(accountUser)
                .account(modifiedAccount)
                .price(1000)
                .build();
        transactionBuild.setRegDate(LocalDateTime.now());

        given(accountUserService.getUser(any())).willReturn(accountUser);
        given(accountService.getAccount(any())).willReturn(account);
        given(accountRepository.save(any())).willReturn(modifiedAccount);
        given(transactionRepository.save(any())).willReturn(transactionBuild);

        Transaction transaction = transactionService.transactionUse(request);

        assertThat(transaction.getPrice()).isEqualTo(1000);
        assertThat(transaction.getTransactionNumber()).isEqualTo("32545");
    }

    @Test
    @DisplayName("잔액 사용 실패 - 가격 차이남")
    void moneyUseFail() {
        TransactionUseDto.Request request = TransactionUseDto.Request.builder()
                .accountNum("3487659102")
                .price(1000)
                .userId("test")
                .build();
        Account accountBuild = account.toBuilder()
                .money(900)
                .build();

        given(accountUserService.getUser(any())).willReturn(accountUser);
        given(accountService.getAccount(any())).willReturn(accountBuild);

        try {
            transactionService.transactionUse(request);
        } catch (TransactionException e) {
            assertThat(e.getMessage()).isEqualTo(TransactionErrorCode.PRICE_MORE_THAN_ACCOUNT_MONEY.getMessage());
        }
    }

    @Test
    @DisplayName("거래 취소 서비스")
    void cancelTransaction() {
        TransactionCancelDto.Request request = TransactionCancelDto.Request.builder()
                .transactionNumber("345678")
                .price(1000)
                .accountNum("12345")
                .build();
        Transaction transaction = Transaction.builder()
                .transactionNumber("345678")
                .transactionResult(TransactionResult.S)
                .transactionType(TransactionType.TRANSACTION)
                .price(1000)
                .accountUser(accountUser)
                .account(account)
                .build();
        Account modifiedAccount = account.toBuilder()
                .money(account.getMoney() + 1000)
                .build();
        Transaction canceledTransaction = Transaction.builder()
                .transactionNumber("3456")
                .transactionResult(TransactionResult.S)
                .transactionType(TransactionType.CANCEL)
                .account(modifiedAccount)
                .accountUser(accountUser)
                .price(1000)
                .build();

        given(transactionRepository.findByTransactionNumber("345678")).willReturn(Optional.of(transaction));
        given(accountService.getAccount(any())).willReturn(account);
        given(accountRepository.save(any())).willReturn(modifiedAccount);
        given(transactionRepository.save(any())).willReturn(canceledTransaction);

        Transaction result = transactionService.canceledTransaction(request);
        assertThat(result.getTransactionNumber()).isEqualTo("3456");
        assertThat(result.getTransactionType()).isEqualTo(TransactionType.CANCEL);
    }

    @Test
    @DisplayName("거래 취소 실패 - 취소하려는 금액과 원거래 금액이 다름")
    void canceledTransactionFailByMoney() {
        TransactionCancelDto.Request request = TransactionCancelDto.Request.builder()
                .transactionNumber("345678")
                .price(1000)
                .accountNum("12345")
                .build();
        Transaction transaction = Transaction.builder()
                .transactionNumber("345678")
                .transactionResult(TransactionResult.S)
                .transactionType(TransactionType.TRANSACTION)
                .price(900)
                .accountUser(accountUser)
                .account(account)
                .build();

        given(transactionRepository.findByTransactionNumber("345678")).willReturn(Optional.of(transaction));
        given(accountService.getAccount(any())).willReturn(account);

        try {
            transactionService.canceledTransaction(request);
        } catch (TransactionException e) {
            assertThat(e.getMessage()).isEqualTo(TransactionErrorCode.DIFF_PRICE_AND_ACCOUNT_MONEY.getMessage());
        }
    }

    @Test
    @DisplayName("거래 취소 실패 - 취소하려는 계좌와 거래한 계좌가 다름")
    void canceledTransactionFailByAccount() {
        TransactionCancelDto.Request request = TransactionCancelDto.Request.builder()
                .transactionNumber("345678")
                .price(1000)
                .accountNum("12345")
                .build();
        Account accountBuild = Account.builder()
                .accountNum("7777")
                .build();
        Transaction transaction = Transaction.builder()
                .transactionNumber("345678")
                .transactionResult(TransactionResult.S)
                .transactionType(TransactionType.TRANSACTION)
                .price(1000)
                .accountUser(accountUser)
                .account(account)
                .build();

        given(transactionRepository.findByTransactionNumber("345678")).willReturn(Optional.of(transaction));
        given(accountService.getAccount(any())).willReturn(accountBuild);

        try {
            transactionService.canceledTransaction(request);
        } catch (TransactionException e) {
            assertThat(e.getMessage()).isEqualTo(TransactionErrorCode.NOT_FOUND_TRANSACTION_NUMBER.getMessage());
        }
    }

    @Test
    @DisplayName("거래 확인 서비스")
    void check() {
        Transaction transaction = Transaction.builder()
                .transactionNumber("34562")
                .transactionResult(TransactionResult.F)
                .price(2000)
                .accountUser(accountUser)
                .account(account)
                .transactionType(TransactionType.CANCEL)
                .build();

        given(transactionRepository.findByTransactionNumber(any())).willReturn(Optional.of(transaction));

        Transaction result = transactionService.getTransaction("34562");

        assertThat(result.getTransactionResult()).isEqualTo(TransactionResult.F);
        assertThat(result.getTransactionNumber()).isEqualTo("34562");
    }

    @Test
    @DisplayName("거래 확인 실패")
    void checkFail() {
        given(transactionRepository.findByTransactionNumber(any())).willThrow(new TransactionException(TransactionErrorCode.NOT_FOUND_TRANSACTION_NUMBER.getMessage()));

        try {
            transactionService.getTransaction("34556");
        } catch (TransactionException e) {
            assertThat(e.getMessage()).isEqualTo(TransactionErrorCode.NOT_FOUND_TRANSACTION_NUMBER.getMessage());
        }
    }
}