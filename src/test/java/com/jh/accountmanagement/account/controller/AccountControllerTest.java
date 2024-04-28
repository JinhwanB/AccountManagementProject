package com.jh.accountmanagement.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.dto.AccountCheckDto;
import com.jh.accountmanagement.account.dto.AccountCreateDto;
import com.jh.accountmanagement.account.dto.AccountDeleteDto;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import com.jh.accountmanagement.account.service.AccountService;
import com.jh.accountmanagement.config.RedisUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @MockBean
    private AccountService accountService;

    @MockBean
    private AccountUserRepository accountUserRepository;

    @MockBean
    private RedisUtils redisUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("계좌 생성 컨트롤러")
    void createAccountController() throws Exception {
        AccountUser accountUser = AccountUser.builder()
                .userId("test")
                .build();
        Account account = Account.builder()
                .accountUser(accountUser)
                .accountNum("3287495760")
                .money(3000)
                .build();
        account.setRegDate(LocalDateTime.now());
        AccountCreateDto.Request request = AccountCreateDto.Request.builder()
                .initMoney(3000)
                .userId("test")
                .build();

        given(accountService.createAccount(any())).willReturn(account);

        mockMvc.perform(post("/accounts/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("test"))
                .andExpect(jsonPath("$.accountNum").value("3287495760"))
                .andExpect(jsonPath("$.regDate").exists());

    }

    @Test
    @DisplayName("계좌 해지 컨트롤러")
    void deleteAccountController() throws Exception {
        AccountUser accountUser = AccountUser.builder()
                .userId("test")
                .build();
        Account account = Account.builder()
                .accountUser(accountUser)
                .accountNum("3287495760")
                .money(3000)
                .delDate(LocalDateTime.now())
                .build();
        AccountDeleteDto.Request request = AccountDeleteDto.Request.builder()
                .userId("test")
                .accountNum("3287495760")
                .build();

        given(accountService.deleteAccount(any())).willReturn(account);

        mockMvc.perform(delete("/accounts/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("test"))
                .andExpect(jsonPath("$.accountNum").value("3287495760"))
                .andExpect(jsonPath("$.delDate").exists());
    }

    @Test
    @DisplayName("계좌 확인 컨트롤러")
    void checkAccount() throws Exception {
        AccountUser accountUser = AccountUser.builder()
                .userId("test")
                .build();
        Account account = Account.builder()
                .accountUser(accountUser)
                .accountNum("3287495760")
                .money(3000)
                .build();
        AccountCheckDto.Request request = AccountCheckDto.Request.builder()
                .userId("test")
                .build();
        List<Account> list = new ArrayList<>(List.of(account));

        given(accountService.checkAccount(any())).willReturn(list);

        mockMvc.perform(get("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNum").value("3287495760"))
                .andExpect(jsonPath("$[0].money").value(3000));
    }
}