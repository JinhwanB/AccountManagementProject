package com.jh.accountmanagement.account.service;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.dto.AccountCreate;
import com.jh.accountmanagement.account.repository.AccountRepository;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
}