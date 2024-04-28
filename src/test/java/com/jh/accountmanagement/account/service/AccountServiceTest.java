package com.jh.accountmanagement.account.service;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.dto.AccountCheckDto;
import com.jh.accountmanagement.account.dto.AccountCreateDto;
import com.jh.accountmanagement.account.dto.AccountDeleteDto;
import com.jh.accountmanagement.account.exception.AccountException;
import com.jh.accountmanagement.account.repository.AccountRepository;
import com.jh.accountmanagement.account.type.AccountErrorCode;
import com.jh.accountmanagement.config.RedisUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RedisUtils redisUtils;

    @Mock
    private AccountUserService accountUserService;

    @InjectMocks
    private AccountService accountService;

    private AccountUser accountUser;
    private Account account;

    @BeforeEach
    void before() {
        accountUser = AccountUser.builder()
                .userId("test")
                .build();
        account = Account.builder()
                .accountNum("3487659102")
                .accountUser(accountUser)
                .money(3000)
                .build();
    }

    @Test
    @DisplayName("계좌 생성")
    void accountCreate() {
        AccountCreateDto.Request request = AccountCreateDto.Request.builder()
                .userId("test")
                .initMoney(1000)
                .build();
        List<Account> list = new ArrayList<>(List.of(account));

        given(accountUserService.getUser(any())).willReturn(accountUser);
        given(accountRepository.findAllByAccountUserAndDelDate(any(), any())).willReturn(list);
        given(accountRepository.save(any())).willReturn(account);

        Account account = accountService.createAccount(request);
        assertThat(account.getAccountNum()).isEqualTo("3487659102");
        assertThat(account.getMoney()).isEqualTo(3000);
        assertThat(account.getAccountUser().getUserId()).isEqualTo("test");
    }

    @Test
    @DisplayName("계좌 생성 실패 - 유저 찾을 수 없음")
    void createAccountFailNotFoundUser() {
        AccountCreateDto.Request request = AccountCreateDto.Request.builder()
                .userId("ddd")
                .initMoney(1000)
                .build();

        given(accountUserService.getUser(any())).willThrow(new AccountException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));

        try {
            accountService.createAccount(request);
        } catch (AccountException e) {
            assertThat(e.getMessage()).isEqualTo(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage());
        }
    }

    @Test
    @DisplayName("계좌 생성 실패 - 계좌 수 10개")
    void createAccountFailAccountNumber() {
        AccountCreateDto.Request request = AccountCreateDto.Request.builder()
                .userId("test")
                .initMoney(1000)
                .build();
        Account[] accounts = new Account[10];
        List<Account> list = new ArrayList<>(Arrays.asList(accounts));

        given(accountUserService.getUser(any())).willReturn(accountUser);
        given(accountRepository.findAllByAccountUserAndDelDate(any(), any())).willReturn(list);

        try {
            accountService.createAccount(request);
        } catch (AccountException e) {
            assertThat(e.getMessage()).isEqualTo(AccountErrorCode.ACCOUNT_MAXIMUM.getMessage());
        }
    }

    @Test
    @DisplayName("계좌 해지")
    void accountDelete() {
        Account modifiedAccount = account.toBuilder()
                .money(0)
                .build();
        AccountDeleteDto.Request request = AccountDeleteDto.Request.builder()
                .accountNum("3254564960")
                .userId("test")
                .build();
        Account deletedAccountBuild = account.toBuilder()
                .delDate(LocalDateTime.now())
                .build();

        given(accountUserService.getUser(any())).willReturn(accountUser);
        given(accountRepository.findByAccountNum(any())).willReturn(Optional.of(modifiedAccount));
        given(accountRepository.save(any())).willReturn(deletedAccountBuild);

        Account deletedAccount = accountService.deleteAccount(request);

        assertThat(deletedAccount.getDelDate()).isNotNull();
    }

    @Test
    @DisplayName("계좌 해지 실패 - 사용자 없음")
    void accountDeleteFailNotUser() {
        AccountDeleteDto.Request request = AccountDeleteDto.Request.builder()
                .accountNum("3254564960")
                .userId("ddd")
                .build();

        given(accountUserService.getUser(any())).willThrow(new AccountException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));

        try {
            accountService.deleteAccount(request);
        } catch (AccountException e) {
            assertThat(e.getMessage()).isEqualTo(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage());
        }
    }

    @Test
    @DisplayName("계좌 해지 실패 - 계좌 정보 불일치")
    void accountDeleteFailNotFoundAccount() {
        AccountDeleteDto.Request request = AccountDeleteDto.Request.builder()
                .accountNum("3254564960")
                .userId("test")
                .build();

        given(accountUserService.getUser(any())).willReturn(accountUser);
        given(accountRepository.findByAccountNum(any())).willThrow(new AccountException(AccountErrorCode.NOT_FOUND_ACCOUNT.getMessage()));

        try {
            accountService.deleteAccount(request);
        } catch (AccountException e) {
            assertThat(e.getMessage()).isEqualTo(AccountErrorCode.NOT_FOUND_ACCOUNT.getMessage());
        }
    }

    @Test
    @DisplayName("계좌 해지 실패 - 유저와 계좌번호 다름")
    void accountDeleteFailDiffUserAccount() {
        AccountDeleteDto.Request request = AccountDeleteDto.Request.builder()
                .accountNum("3254564960")
                .userId("test")
                .build();
        AccountUser accountUserBuild = AccountUser.builder()
                .userId("ddd")
                .build();
        Account accountBuild = Account.builder()
                .accountUser(accountUserBuild)
                .accountNum("3445")
                .build();

        given(accountUserService.getUser(any())).willReturn(accountUser);
        given(accountRepository.findByAccountNum(any())).willReturn(Optional.of(accountBuild));

        try {
            accountService.deleteAccount(request);
        } catch (AccountException e) {
            assertThat(e.getMessage()).isEqualTo(AccountErrorCode.DIFF_USER_AND_ACCOUNT_NUMBER.getMessage());
        }
    }

    @Test
    @DisplayName("계좌 해지 실패 - 이미 해지된 계좌")
    void deleteAccountFailAlreadyDeleted() {
        AccountDeleteDto.Request request = AccountDeleteDto.Request.builder()
                .accountNum("3254564960")
                .userId("test")
                .build();
        Account accountBuild = account.toBuilder()
                .delDate(LocalDateTime.now())
                .build();

        given(accountUserService.getUser(any())).willReturn(accountUser);
        given(accountRepository.findByAccountNum(any())).willReturn(Optional.of(accountBuild));

        try {
            accountService.deleteAccount(request);
        } catch (AccountException e) {
            assertThat(e.getMessage()).isEqualTo(AccountErrorCode.ALREADY_DELETED_ACCOUNT.getMessage());
        }
    }

    @Test
    @DisplayName("계좌 해지 실패 - 계좌 잔액 남아있음")
    void accountDeleteFailMoney() {
        AccountDeleteDto.Request request = AccountDeleteDto.Request.builder()
                .accountNum("3254564960")
                .userId("test")
                .build();
        Account accountBuild = account.toBuilder()
                .money(1000)
                .build();

        given(accountUserService.getUser(any())).willReturn(accountUser);
        given(accountRepository.findByAccountNum(any())).willReturn(Optional.of(accountBuild));

        try {
            accountService.deleteAccount(request);
        } catch (AccountException e) {
            assertThat(e.getMessage()).isEqualTo(AccountErrorCode.DELETE_ACCOUNT_FAIL.getMessage());
        }
    }

    @Test
    @DisplayName("계좌 확인")
    void checkAccount() {
        AccountCheckDto.Request request = AccountCheckDto.Request.builder()
                .userId("test")
                .build();
        List<Account> list = new ArrayList<>(List.of(account));

        given(accountUserService.getUser(any())).willReturn(accountUser);
        given(accountRepository.findAllByAccountUserAndDelDate(any(), any())).willReturn(list);

        List<Account> accounts = accountService.checkAccount(request);
        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getAccountNum()).isEqualTo("3487659102");
    }

    @Test
    @DisplayName("계좌 확인 실패 - 유저 없음")
    void checkAccountFailUser() {
        AccountCheckDto.Request request = AccountCheckDto.Request.builder()
                .userId("test")
                .build();

        given(accountUserService.getUser(any())).willThrow(new AccountException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));

        try {
            accountService.checkAccount(request);
        } catch (AccountException e) {
            assertThat(e.getMessage()).isEqualTo(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage());
        }
    }
}