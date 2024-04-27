package com.jh.accountmanagement.transaction.controller;

import com.jh.accountmanagement.transaction.domain.Transaction;
import com.jh.accountmanagement.transaction.dto.TransactionUseDto;
import com.jh.accountmanagement.transaction.exception.TransactionPriceException;
import com.jh.accountmanagement.transaction.service.TransactionService;
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
@RequestMapping("/transactions")
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transaction")
    public ResponseEntity<TransactionUseDto.Response> useMoney(@Valid @RequestBody TransactionUseDto.Request request) {
        Transaction transaction;
        try {
            transaction = transactionService.transactionUse(request);
        } catch (TransactionPriceException e) {
            log.error(e.getMessage());
            transaction = transactionService.useFail(request);
        }
        return ResponseEntity.ok(transaction.toUseResponse());
    }
}
