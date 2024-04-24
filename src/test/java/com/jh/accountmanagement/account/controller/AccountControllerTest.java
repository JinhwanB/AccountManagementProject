package com.jh.accountmanagement.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.dto.AccountCreate;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}