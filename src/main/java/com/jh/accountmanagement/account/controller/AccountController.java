package com.jh.accountmanagement.account.controller;

import com.jh.accountmanagement.account.dto.AccountCreate;
import com.jh.accountmanagement.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/account")
    public ResponseEntity<AccountCreate.Response> createAccount(@Valid @RequestBody AccountCreate.Request request) {
        return ResponseEntity.ok(accountService.createAccount(request));
    }
}
