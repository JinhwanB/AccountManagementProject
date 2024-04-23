package com.jh.accountmanagement.account.service;

import com.jh.accountmanagement.account.dto.AccountCreate;
import com.jh.accountmanagement.account.exception.NotFoundUserIdException;
import com.jh.accountmanagement.account.model.AccountUser;
import com.jh.accountmanagement.account.repository.AccountRepository;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class AccountServiceTest {
    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountUserRepository accountUserRepository;

    @Test
    @DisplayName("계좌 생성 테스트")
    void createAccount() {
        // given
        AccountUser entity = AccountUser.builder()
                .userId("test")
                .build();
        accountUserRepository.save(entity);

        AccountCreate.Request request = AccountCreate.Request.builder()
                .initMoney(1000L)
                .userId("test")
                .build();

        //when
        accountService.createAccount(request);

        // then
        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new NotFoundUserIdException("해당 유저는 없습니다."));
        assertThat(accountUser.getUserId()).isEqualTo("test");
        assertThat(accountUser.getAccountList()).hasSize(1);
        assertThat(accountUser.getAccountList().get(0).getMoney()).isEqualTo(1000L);
    }
}