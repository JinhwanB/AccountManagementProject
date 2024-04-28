package com.jh.accountmanagement.account.controller;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.dto.AccountCheckDto;
import com.jh.accountmanagement.account.dto.AccountCreateDto;
import com.jh.accountmanagement.account.dto.AccountDeleteDto;
import com.jh.accountmanagement.account.dto.AccountRedisDto;
import com.jh.accountmanagement.account.service.AccountService;
import com.jh.accountmanagement.config.RedisUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;
    private final RedisUtils redisUtils;

    // 계좌 생성 컨트롤러
    @PostMapping("/account")
    public ResponseEntity<AccountCreateDto.Response> createAccount(@Valid @RequestBody AccountCreateDto.Request request) {
        Account account = accountService.createAccount(request);
        return ResponseEntity.ok(account.toCreateResponse());
    }

    // 계좌 해지 컨트롤러
    @DeleteMapping("/account")
    public ResponseEntity<AccountDeleteDto.Response> deleteAccount(@Valid @RequestBody AccountDeleteDto.Request request) {
        Account account = accountService.deleteAccount(request);
        return ResponseEntity.ok(account.toDeleteResponse());
    }

    // 계좌 확인 컨트롤러
    @GetMapping
    public ResponseEntity<List<AccountCheckDto.Response>> checkAccount(@Valid @RequestBody AccountCheckDto.Request request) {
        if (redisUtils.hasKeyOfAccount(request.getUserId())) {
            List<AccountRedisDto> accounts = redisUtils.getAccount(request.getUserId());
            return ResponseEntity.ok(accounts.stream().map(AccountRedisDto::toCheckResponse).toList());
        }

        List<Account> accounts = accountService.checkAccount(request);
        for (Account account : accounts) {
            redisUtils.setAccount(request.getUserId(), account.toRedisDto());
        }
        return ResponseEntity.ok(accounts.stream().map(Account::toCheckResponse).toList());
    }
}
