package com.jh.accountmanagement.account.controller;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.dto.AccountCheck;
import com.jh.accountmanagement.account.dto.AccountCreate;
import com.jh.accountmanagement.account.dto.AccountDelete;
import com.jh.accountmanagement.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    // 계좌 생성 컨트롤러
    @PostMapping("/account")
    public ResponseEntity<AccountCreate.Response> createAccount(@Valid @RequestBody AccountCreate.Request request) {
        Account account = accountService.createAccount(request);
        return ResponseEntity.ok(account.toCreateResponse());
    }

    // 계좌 해지 컨트롤러
    @DeleteMapping("/account")
    public ResponseEntity<AccountDelete.Response> deleteAccount(@Valid @RequestBody AccountDelete.Request request) {
        Account account = accountService.deleteAccount(request);
        return ResponseEntity.ok(account.toDeleteResponse());
    }

    // 계좌 확인 컨트롤러
    @GetMapping
    public ResponseEntity<List<AccountCheck.Response>> checkAccount(@Valid @RequestBody AccountCheck.Request request) {
        List<Account> accounts = accountService.checkAccount(request);
        return ResponseEntity.ok(accounts.stream().map(Account::toCheckResponse).toList());
    }
}
