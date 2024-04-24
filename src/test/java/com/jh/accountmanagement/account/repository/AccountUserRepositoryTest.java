package com.jh.accountmanagement.account.repository;

import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.exception.NotFoundUserIdException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountUserRepositoryTest {
    @Autowired
    private AccountUserRepository accountUserRepository;

    @BeforeEach
    void before() {
        AccountUser accountUserBuild = AccountUser.builder()
                .userId("test")
                .build();
        accountUserBuild.setRegDate(LocalDateTime.now());
        accountUserBuild.setChgDate(LocalDateTime.now());
        accountUserRepository.save(accountUserBuild);
    }

    @Test
    @DisplayName("사용자 확인")
    void userCheck() {
        AccountUser user = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new NotFoundUserIdException("해당 아이디의 유저는 없습니다."));

        assertThat(user.getUserId()).isEqualTo("test");
    }
}