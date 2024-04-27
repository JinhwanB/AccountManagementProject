package com.jh.accountmanagement.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import com.jh.accountmanagement.transaction.domain.Transaction;
import com.jh.accountmanagement.transaction.dto.TransactionUseDto;
import com.jh.accountmanagement.transaction.exception.TransactionPriceException;
import com.jh.accountmanagement.transaction.service.TransactionService;
import com.jh.accountmanagement.transaction.type.TransactionResult;
import com.jh.accountmanagement.transaction.type.TransactionType;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AccountUserRepository accountUserRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    AccountUser accountUser;
    Account account;

    @BeforeEach
    void before() {
        accountUser = AccountUser.builder()
                .userId("test")
                .build();
        account = Account.builder()
                .accountNum(12345)
                .money(5000)
                .accountUser(accountUser)
                .build();
    }

    @Test
    @DisplayName("잔액 사용 컨트롤러")
    void useMoney() throws Exception {
        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.TRANSACTION)
                .transactionResult(TransactionResult.S)
                .transactionNumber("12345")
                .accountUser(accountUser)
                .price(1000)
                .account(account)
                .build();
        transaction.setRegDate(LocalDateTime.now());
        TransactionUseDto.Request request = TransactionUseDto.Request.builder()
                .userId("test")
                .price(1000)
                .accountNum(12345)
                .build();

        given(transactionService.transactionUse(any())).willReturn(transaction);

        mockMvc.perform(post("/transactions/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNum").value(12345))
                .andExpect(jsonPath("$.transactionResult").value(TransactionResult.S.getMessage()))
                .andExpect(jsonPath("$.transactionNumber").value("12345"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.regDate").exists());
    }

    @Test
    @DisplayName("잔액 사용 컨트롤러 실패")
    void useMoneyFail() throws Exception {
        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.TRANSACTION)
                .transactionResult(TransactionResult.F)
                .transactionNumber("12345")
                .accountUser(accountUser)
                .price(1000)
                .account(account)
                .build();
        transaction.setRegDate(LocalDateTime.now());
        TransactionUseDto.Request request = TransactionUseDto.Request.builder()
                .userId("test")
                .price(1000)
                .accountNum(12345)
                .build();

        given(transactionService.transactionUse(any())).willThrow(TransactionPriceException.class);
        given(transactionService.useFail(any())).willReturn(transaction);

        mockMvc.perform(post("/transactions/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNum").value(12345))
                .andExpect(jsonPath("$.transactionResult").value(TransactionResult.F.getMessage()))
                .andExpect(jsonPath("$.transactionNumber").value("12345"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.regDate").exists());
    }
}