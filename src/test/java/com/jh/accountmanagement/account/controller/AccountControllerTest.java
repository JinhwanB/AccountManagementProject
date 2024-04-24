package com.jh.accountmanagement.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.dto.AccountCheck;
import com.jh.accountmanagement.account.dto.AccountCreate;
import com.jh.accountmanagement.account.dto.AccountDelete;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import com.jh.accountmanagement.account.service.AccountService;
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
                .accountNum(3287495760L)
                .money(3000)
                .build();
        account.setRegDate(LocalDateTime.now());
        AccountCreate.Request request = AccountCreate.Request.builder()
                .initMoney(3000)
                .userId("test")
                .build();

        given(accountService.createAccount(any())).willReturn(account);

        mockMvc.perform(post("/accounts/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("test"))
                .andExpect(jsonPath("$.accountNum").value(3287495760L))
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
                .accountNum(3287495760L)
                .money(3000)
                .delDate(LocalDateTime.now())
                .build();
        AccountDelete.Request request = AccountDelete.Request.builder()
                .userId("test")
                .accountNum(3287495760L)
                .build();

        given(accountService.deleteAccount(any())).willReturn(account);

        mockMvc.perform(delete("/accounts/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("test"))
                .andExpect(jsonPath("$.accountNum").value(3287495760L))
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
                .accountNum(3287495760L)
                .money(3000)
                .build();
        AccountCheck.Request request = AccountCheck.Request.builder()
                .userId("test")
                .build();
        List<Account> list = new ArrayList<>(List.of(account));

        given(accountService.checkAccount(any())).willReturn(list);

        mockMvc.perform(get("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNum").value(3287495760L))
                .andExpect(jsonPath("$[0].money").value(3000));
    }
}