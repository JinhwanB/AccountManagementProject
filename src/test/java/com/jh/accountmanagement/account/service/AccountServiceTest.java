package com.jh.accountmanagement.account.service;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.dto.AccountCheck;
import com.jh.accountmanagement.account.dto.AccountCreate;
import com.jh.accountmanagement.account.dto.AccountDelete;
import com.jh.accountmanagement.account.repository.AccountRepository;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountService accountService;

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
    @DisplayName("계좌 생성")
    void accountCreate() {
        AccountCreate.Request request = AccountCreate.Request.builder()
                .userId("Test")
                .initMoney(1000)
                .build();
        List<Account> list = new ArrayList<>(List.of(account));

        given(accountUserRepository.findByUserIdAndDelDate(any(), any())).willReturn(Optional.of(accountUser));
        given(accountRepository.findAllByAccountUserAndDelDate(any(), any())).willReturn(list);
        given(accountRepository.save(any())).willReturn(account);

        Account account = accountService.createAccount(request);
        assertThat(account.getAccountNum()).isEqualTo(3487659102L);
        assertThat(account.getMoney()).isEqualTo(3000);
        assertThat(account.getAccountUser().getUserId()).isEqualTo("test");
    }

    @Test
    @DisplayName("계좌 해지")
    void accountDelete() {
        Account modifiedAccount = account.toBuilder()
                .money(0)
                .build();
        AccountDelete.Request request = AccountDelete.Request.builder()
                .accountNum(3254564960L)
                .userId("test")
                .build();
        Account deletedAccountBuild = account.toBuilder()
                .delDate(LocalDateTime.now())
                .build();

        given(accountUserRepository.findByUserIdAndDelDate(any(), any())).willReturn(Optional.of(accountUser));
        given(accountRepository.findByAccountUserAndAccountNum(any(), anyLong())).willReturn(Optional.of(modifiedAccount));
        given(accountRepository.save(any())).willReturn(deletedAccountBuild);

        Account deletedAccount = accountService.deleteAccount(request);
        assertThat(deletedAccount.getDelDate()).isNotNull();
    }

    @Test
    @DisplayName("계좌 확인")
    void checkAccount() {
        AccountCheck.Request request = AccountCheck.Request.builder()
                .userId("test")
                .build();
        List<Account> list = new ArrayList<>(List.of(account));

        given(accountUserRepository.findByUserIdAndDelDate(any(), any())).willReturn(Optional.of(accountUser));
        given(accountRepository.findAllByAccountUserAndDelDate(any(), any())).willReturn(list);

        List<Account> accounts = accountService.checkAccount(request);
        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getAccountNum()).isEqualTo(3487659102L);
    }
}