package com.jh.accountmanagement.account.service;

import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.exception.AccountException;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import com.jh.accountmanagement.account.type.AccountErrorCode;
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

    public AccountUser getUser(String userId) {
        log.info("사용자 아이디={}", userId);

        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate(userId, null).orElseThrow(() -> new AccountException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));
        return accountUser;
    }
}
