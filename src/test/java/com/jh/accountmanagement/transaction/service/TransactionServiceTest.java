package com.jh.accountmanagement.transaction.service;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.repository.AccountRepository;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import com.jh.accountmanagement.transaction.domain.Transaction;
import com.jh.accountmanagement.transaction.dto.TransactionUseDto;
import com.jh.accountmanagement.transaction.repository.TransactionRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @Mock
    private AccountRepository accountRepository;

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
                .accountNum(3487659102L)
                .accountUser(accountUser)
                .money(3000)
                .build();
    }

    @Test
    @DisplayName("잔액 사용 서비스 테스트")
    void accountMoneyUse() {
        TransactionUseDto.Request request = TransactionUseDto.Request.builder()
                .accountNum(3487659102L)
                .price(1000)
                .userId("test")
                .build();
        Account modifiedAccount = Account.builder()
                .accountNum(3487659102L)
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

        given(accountUserRepository.findByUserIdAndDelDate(any(), any())).willReturn(Optional.of(accountUser));
        given(accountRepository.findByAccountUserAndAccountNum(any(), anyLong())).willReturn(Optional.of(account));
        given(accountRepository.save(any())).willReturn(modifiedAccount);
        given(transactionRepository.save(any())).willReturn(transactionBuild);

        Transaction transaction = transactionService.transactionUse(request);

        assertThat(transaction.getPrice()).isEqualTo(1000);
        assertThat(transaction.getTransactionNumber()).isEqualTo("32545");
    }
}