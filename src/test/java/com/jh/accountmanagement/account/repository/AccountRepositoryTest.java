package com.jh.accountmanagement.account.repository;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.exception.*;
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
        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new NotFoundUserIdException("해당 유저는 없습니다."));
        Account accountBuild = Account.builder()
                .accountNum(1234567893)
                .accountUser(accountUser)
                .money(1000)
                .build();
        accountBuild.setRegDate(LocalDateTime.now());
        accountBuild.setChgDate(LocalDateTime.now());
        Account account = accountRepository.save(accountBuild);

        assertThat(account.getAccountNum()).isEqualTo(1234567893);
        assertThat(account.getAccountUser().getUserId()).isEqualTo("test");
        assertThat(account.getMoney()).isEqualTo(1000);
    }

    @Test
    @DisplayName("계좌 해지")
    void accountDelete() {
        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new NotFoundUserIdException("해당 유저는 없습니다."));
        Account accountBuild = Account.builder()
                .accountNum(1234567893)
                .accountUser(accountUser)
                .money(1000)
                .build();
        accountBuild.setRegDate(LocalDateTime.now());
        accountBuild.setChgDate(LocalDateTime.now());
        accountRepository.save(accountBuild);

        Account getAccount = accountRepository.findByAccountUserAndAccountNum(accountUser, 1234567893).orElseThrow(() -> new NotFoundAccountException("해당 유저의 계좌는 없습니다."));
        if (getAccount.getDelDate() != null) {
            throw new AlreadyDeletedAccountException("이미 해지된 계좌입니다.");
        }

        Account deletedAccountBuild = getAccount.toBuilder()
                .delDate(LocalDateTime.now())
                .build();
        Account deletedAccount = accountRepository.save(deletedAccountBuild);

        assertThat(deletedAccount.getAccountUser().getUserId()).isEqualTo("test");
        assertThat(deletedAccount.getAccountNum()).isEqualTo(1234567893);
        assertThat(deletedAccount.getDelDate()).isNotNull();
    }

    @Test
    @DisplayName("사용자의 혜지되지 않은 계좌 리스트 가져오기 : 계좌 확인")
    void accountList() {
        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new NotFoundUserIdException("해당 유저는 없습니다."));
        Account accountBuild = Account.builder()
                .accountNum(1234567893)
                .accountUser(accountUser)
                .money(1000)
                .build();
        accountBuild.setRegDate(LocalDateTime.now());
        accountBuild.setChgDate(LocalDateTime.now());
        accountRepository.save(accountBuild);

        List<Account> accountList = accountRepository.findAllByAccountUserAndDelDate(accountUser, null);
        assertThat(accountList).hasSize(1);
        assertThat(accountList.get(0).getAccountNum()).isEqualTo(1234567893);
    }

    @Test
    @DisplayName("계좌 10개 이상인 경우 exception")
    void accountMaximumException() {
        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new NotFoundUserIdException("해당 유저는 없습니다."));
        long accountNum = 1234567893;
        for (int i = 0; i < 10; i++) {
            Account accountBuild = Account.builder()
                    .accountNum(accountNum++)
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
                throw new AccountMaximumException("계좌는 최대 10개까지만 만들 수 있습니다.");
            }
        } catch (AccountMaximumException e) {
            assertThat(e.getMessage()).isEqualTo("계좌는 최대 10개까지만 만들 수 있습니다.");
        }
    }

    @Test
    @DisplayName("계좌가 해지상태인 경우 Exception")
    void AlreadyDeleteAccount() {
        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new NotFoundUserIdException("해당 유저는 없습니다."));
        Account accountBuild = Account.builder()
                .accountNum(1234567893)
                .accountUser(accountUser)
                .money(1000)
                .delDate(LocalDateTime.now())
                .build();
        accountBuild.setRegDate(LocalDateTime.now());
        accountBuild.setChgDate(LocalDateTime.now());
        accountRepository.save(accountBuild);

        try {
            Account account = accountRepository.findByAccountNum(1234567893).orElseThrow(() -> new NotFoundAccountException("해당 계좌번호와 일치하는 계좌가 없습니다."));
            if (account.getDelDate() != null) {
                throw new AlreadyDeletedAccountException(AccountErrorCode.ALREADY_DELETED_ACCOUNT.getMessage());
            }
        } catch (AlreadyDeletedAccountException e) {
            assertThat(e.getMessage()).isEqualTo("이미 해지된 계좌입니다.");
        }
    }

    @Test
    @DisplayName("잔액이 남아있을 때 해지 시 exception")
    void FailDeleteAccount() {
        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate("test", null).orElseThrow(() -> new NotFoundUserIdException("해당 유저는 없습니다."));
        Account accountBuild = Account.builder()
                .accountNum(1234567893)
                .accountUser(accountUser)
                .money(1000)
                .build();
        accountBuild.setRegDate(LocalDateTime.now());
        accountBuild.setChgDate(LocalDateTime.now());
        accountRepository.save(accountBuild);

        try {
            Account account = accountRepository.findByAccountNum(1234567893).orElseThrow(() -> new NotFoundAccountException("해당 계좌번호와 일치하는 계좌가 없습니다."));
            if (account.getMoney() != 0) {
                throw new DeleteAccountFailException("계좌에 잔액이 남아있어 해지하지 못합니다.");
            }
        } catch (DeleteAccountFailException e) {
            assertThat(e.getMessage()).isEqualTo("계좌에 잔액이 남아있어 해지하지 못합니다.");
        }
    }
}