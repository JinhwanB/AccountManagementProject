package com.jh.accountmanagement.account.service;

import com.jh.accountmanagement.account.dto.AccountCreate;
import com.jh.accountmanagement.account.model.Account;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class AccountServiceTest {
    @Autowired
    AccountService accountService;

    @Autowired
    AccountUserRepository accountUserRepository;

    @Test
    @DisplayName("계좌 생성 테스트")
    void createAccount() {
        // given
        AccountCreate.Request request = AccountCreate.Request.builder()
                .initMoney(1000L)
                .userId("test")
                .build();
        AccountCreate.Request request2 = AccountCreate.Request.builder()
                .initMoney(10000L)
                .userId("test")
                .build();

        //when
        // then
        Account account = accountService.createAccount(request);
        assertThat(account.getAccountUser().getUserId()).isEqualTo("test");
        assertThat(account.getAccountUser().getAccountList()).hasSize(1);
        assertThat(account.getAccountUser().getAccountList().get(0).getMoney()).isEqualTo(1000L);

        Account account2 = accountService.createAccount(request2);
        assertThat(account2.getAccountUser().getUserId()).isEqualTo("test");
        assertThat(account2.getAccountUser().getAccountList()).hasSize(2);
        assertThat(account2.getAccountUser().getAccountList().get(1).getMoney()).isEqualTo(10000L);
    }
}