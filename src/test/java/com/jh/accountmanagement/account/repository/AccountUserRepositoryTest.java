package com.jh.accountmanagement.account.repository;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.exception.NotFoundAccountException;
import com.jh.accountmanagement.account.exception.NotFoundUserIdException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class AccountUserRepositoryTest {
    @Autowired
    private AccountUserRepository accountUserRepository;

    @Autowired
    private AccountRepository accountRepository;

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

    @Test
    @DisplayName("사용자 찾기 실패")
    void failAccountUserId() {
        NotFoundUserIdException exception = assertThrows(NotFoundUserIdException.class, () -> accountUserRepository.findByUserIdAndDelDate("toast", null).orElseThrow(() -> new NotFoundUserIdException("해당 유저는 없습니다.")));
        assertThat(exception.getMessage()).isEqualTo("해당 유저는 없습니다.");
    }

    @Test
    @DisplayName("사용자와 계좌번호 불일치")
    void diffAccountUserAndAccountNum() {
        AccountUser user = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new NotFoundUserIdException("해당 아이디의 유저는 없습니다."));
        Account account = Account.builder()
                .accountNum(3249587201L)
                .accountUser(user)
                .money(30000)
                .build();
        account.setRegDate(LocalDateTime.now());
        account.setChgDate(LocalDateTime.now());
        accountRepository.save(account);

        try {
            accountRepository.findByAccountUserAndAccountNum(user, 3234586593L).orElseThrow(() -> new NotFoundAccountException("해당 계좌번호는 사용자의 계좌번호가 아닙니다."));
        } catch (NotFoundAccountException e) {
            assertThat(e.getMessage()).isEqualTo("해당 계좌번호는 사용자의 계좌번호가 아닙니다.");
        }
    }
}