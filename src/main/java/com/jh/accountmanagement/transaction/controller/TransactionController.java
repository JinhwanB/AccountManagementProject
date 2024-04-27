package com.jh.accountmanagement.transaction.controller;

import com.jh.accountmanagement.transaction.domain.Transaction;
import com.jh.accountmanagement.transaction.dto.TransactionUseDto;
import com.jh.accountmanagement.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transaction")
    public ResponseEntity<TransactionUseDto.Response> useMoney(@Valid @RequestBody TransactionUseDto.Request request) {
        Transaction transaction = transactionService.transactionUse(request);
        return ResponseEntity.ok(transaction.toUseResponse());
    }
}
