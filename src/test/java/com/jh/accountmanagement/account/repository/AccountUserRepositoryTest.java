package com.jh.accountmanagement.account.repository;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.exception.AccountException;
import com.jh.accountmanagement.account.type.AccountErrorCode;
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

    @Test
    @DisplayName("사용자 확인")
    void userCheck() {
        AccountUser user = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new AccountException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));

        assertThat(user.getUserId()).isEqualTo("test");
    }

    @Test
    @DisplayName("사용자 찾기 실패")
    void failAccountUserId() {
        AccountException exception = assertThrows(AccountException.class, () -> accountUserRepository.findByUserIdAndDelDate("toast", null).orElseThrow(() -> new AccountException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage())));
        assertThat(exception.getMessage()).isEqualTo(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage());
    }

    @Test
    @DisplayName("사용자와 계좌번호 불일치")
    void diffAccountUserAndAccountNum() {
        AccountUser user = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new AccountException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));
        Account account = Account.builder()
                .accountNum("3249587201")
                .accountUser(user)
                .money(30000)
                .build();
        account.setRegDate(LocalDateTime.now());
        account.setChgDate(LocalDateTime.now());
        accountRepository.save(account);

        try {
            accountRepository.findByAccountUserAndAccountNum(user, "3234586593").orElseThrow(() -> new AccountException(AccountErrorCode.DIFF_USER_AND_ACCOUNT_NUMBER.getMessage()));
        } catch (AccountException e) {
            assertThat(e.getMessage()).isEqualTo(AccountErrorCode.DIFF_USER_AND_ACCOUNT_NUMBER.getMessage());
        }
    }
}