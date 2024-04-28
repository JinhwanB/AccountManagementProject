package com.jh.accountmanagement.account.service;

import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.exception.AccountException;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import com.jh.accountmanagement.account.type.AccountErrorCode;
import com.jh.accountmanagement.config.RedisUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AccountUserServiceTest {
    @Mock
    private AccountUserRepository accountUserRepository;

    @Mock
    private RedisUtils redisUtils;

    @InjectMocks
    private AccountUserService accountUserService;

    @Test
    @DisplayName("유저 찾기")
    void findUser() {
        AccountUser accountUser = AccountUser.builder()
                .userId("test")
                .build();

        given(accountUserRepository.findByUserIdAndDelDate(any(), any())).willReturn(Optional.of(accountUser));

        AccountUser user = accountUserService.getUser("test");

        assertThat(user.getUserId()).isEqualTo("test");
    }

    @Test
    @DisplayName("유저 찾기 실패")
    void findUserFail() {
        given(accountUserRepository.findByUserIdAndDelDate(any(), any())).willThrow(new AccountException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));

        try {
            accountUserService.getUser("test");
        } catch (AccountException e) {
            assertThat(e.getMessage()).isEqualTo(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage());
        }
    }
}