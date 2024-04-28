package com.jh.accountmanagement.account.repository;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.exception.AccountException;
import com.jh.accountmanagement.account.type.AccountErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

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
    @DisplayName("계좌 생성")
    void accountCreate() {
        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new AccountException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));
        Account accountBuild = Account.builder()
                .accountNum("1234567893")
                .accountUser(accountUser)
                .money(1000)
                .build();
        accountBuild.setRegDate(LocalDateTime.now());
        accountBuild.setChgDate(LocalDateTime.now());
        Account account = accountRepository.save(accountBuild);

        assertThat(account.getAccountNum()).isEqualTo("1234567893");
        assertThat(account.getAccountUser().getUserId()).isEqualTo("test");
        assertThat(account.getMoney()).isEqualTo(1000);
    }

    @Test
    @DisplayName("계좌 해지")
    void accountDelete() {
        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new AccountException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));
        Account accountBuild = Account.builder()
                .accountNum("1234567893")
                .accountUser(accountUser)
                .money(1000)
                .build();
        accountBuild.setRegDate(LocalDateTime.now());
        accountBuild.setChgDate(LocalDateTime.now());
        accountRepository.save(accountBuild);

        Account getAccount = accountRepository.findByAccountUserAndAccountNum(accountUser, "1234567893").orElseThrow(() -> new AccountException(AccountErrorCode.NOT_FOUND_ACCOUNT.getMessage()));
        if (getAccount.getDelDate() != null) {
            throw new AccountException(AccountErrorCode.ALREADY_DELETED_ACCOUNT.getMessage());
        }

        Account deletedAccountBuild = getAccount.toBuilder()
                .delDate(LocalDateTime.now())
                .build();
        Account deletedAccount = accountRepository.save(deletedAccountBuild);

        assertThat(deletedAccount.getAccountUser().getUserId()).isEqualTo("test");
        assertThat(deletedAccount.getAccountNum()).isEqualTo("1234567893");
        assertThat(deletedAccount.getDelDate()).isNotNull();
    }

    @Test
    @DisplayName("사용자의 혜지되지 않은 계좌 리스트 가져오기 : 계좌 확인")
    void accountList() {
        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new AccountException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));
        Account accountBuild = Account.builder()
                .accountNum("1234567893")
                .accountUser(accountUser)
                .money(1000)
                .build();
        accountBuild.setRegDate(LocalDateTime.now());
        accountBuild.setChgDate(LocalDateTime.now());
        accountRepository.save(accountBuild);

        List<Account> accountList = accountRepository.findAllByAccountUserAndDelDate(accountUser, null);
        assertThat(accountList).hasSize(1);
        assertThat(accountList.get(0).getAccountNum()).isEqualTo("1234567893");
    }

    @Test
    @DisplayName("계좌 10개 이상인 경우 exception")
    void accountMaximumException() {
        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new AccountException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));
        long accountNum = 1234567893;
        for (int i = 0; i < 10; i++) {
            Account accountBuild = Account.builder()
                    .accountNum(String.valueOf(accountNum++))
                    .accountUser(accountUser)
                    .money(1000)
                    .build();
            accountBuild.setRegDate(LocalDateTime.now());
            accountBuild.setChgDate(LocalDateTime.now());
            accountRepository.save(accountBuild);
        }
        List<Account> accountList = accountRepository.findAllByAccountUserAndDelDate(accountUser, null);
        try {
            if (accountList.size() == 10) {
                throw new AccountException(AccountErrorCode.ACCOUNT_MAXIMUM.getMessage());
            }
        } catch (AccountException e) {
            assertThat(e.getMessage()).isEqualTo(AccountErrorCode.ACCOUNT_MAXIMUM.getMessage());
        }
    }

    @Test
    @DisplayName("계좌가 해지상태인 경우 Exception")
    void AlreadyDeleteAccount() {
        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new AccountException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));
        Account accountBuild = Account.builder()
                .accountNum("1234567893")
                .accountUser(accountUser)
                .money(1000)
                .delDate(LocalDateTime.now())
                .build();
        accountBuild.setRegDate(LocalDateTime.now());
        accountBuild.setChgDate(LocalDateTime.now());
        accountRepository.save(accountBuild);

        try {
            Account account = accountRepository.findByAccountNum("1234567893").orElseThrow(() -> new AccountException(AccountErrorCode.NOT_FOUND_ACCOUNT.getMessage()));
            if (account.getDelDate() != null) {
                throw new AccountException(AccountErrorCode.ALREADY_DELETED_ACCOUNT.getMessage());
            }
        } catch (AccountException e) {
            assertThat(e.getMessage()).isEqualTo(AccountErrorCode.ALREADY_DELETED_ACCOUNT.getMessage());
        }
    }

    @Test
    @DisplayName("잔액이 남아있을 때 해지 시 exception")
    void FailDeleteAccount() {
        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new AccountException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));
        Account accountBuild = Account.builder()
                .accountNum("1234567893")
                .accountUser(accountUser)
                .money(1000)
                .build();
        accountBuild.setRegDate(LocalDateTime.now());
        accountBuild.setChgDate(LocalDateTime.now());
        accountRepository.save(accountBuild);

        try {
            Account account = accountRepository.findByAccountNum("1234567893").orElseThrow(() -> new AccountException(AccountErrorCode.NOT_FOUND_ACCOUNT.getMessage()));
            if (account.getMoney() != 0) {
                throw new AccountException(AccountErrorCode.DELETE_ACCOUNT_FAIL.getMessage());
            }
        } catch (AccountException e) {
            assertThat(e.getMessage()).isEqualTo(AccountErrorCode.DELETE_ACCOUNT_FAIL.getMessage());
        }
    }
}