package com.jh.accountmanagement.account.service;

import com.jh.accountmanagement.account.dto.AccountCreate;
import com.jh.accountmanagement.account.exception.NotFoundUserIdException;
import com.jh.accountmanagement.account.model.Account;
import com.jh.accountmanagement.account.model.AccountUser;
import com.jh.accountmanagement.account.repository.AccountRepository;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountUserRepository accountUserRepository;

    @Autowired
    private AccountRepository accountRepository;

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
        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new NotFoundUserIdException("해당 아이디의 유저는 없습니다."));
        List<Account> accountList = accountRepository.findAllByAccountUserAndDelDate(accountUser, null);
        assertThat(account.getAccountUser().getUserId()).isEqualTo("test");
        assertThat(accountList).hasSize(1);
        assertThat(accountList.get(0).getMoney()).isEqualTo(1000L);

        Account account2 = accountService.createAccount(request2);
        List<Account> accountList2 = accountRepository.findAllByAccountUserAndDelDate(accountUser, null);
        assertThat(account2.getAccountUser().getUserId()).isEqualTo("test");
        assertThat(accountList2).hasSize(2);
    }
}