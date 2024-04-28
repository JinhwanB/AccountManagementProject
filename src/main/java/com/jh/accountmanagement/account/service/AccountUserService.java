package com.jh.accountmanagement.account.service;

import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.exception.AccountException;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import com.jh.accountmanagement.account.type.AccountErrorCode;
import com.jh.accountmanagement.config.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountUserService {
    private final AccountUserRepository accountUserRepository;
    private final RedisUtils redisUtils;

    public AccountUser getUser(String userId) {
        log.info("사용자 아이디={}", userId);

        if (redisUtils.hasKey(userId)) {
            return redisUtils.get(userId);
        }

        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate(userId, null).orElseThrow(() -> new AccountException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));
        redisUtils.userSet(userId, accountUser);
        return accountUser;
    }
}
