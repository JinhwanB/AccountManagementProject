package com.jh.accountmanagement.transaction.controller;

import com.jh.accountmanagement.transaction.domain.Transaction;
import com.jh.accountmanagement.transaction.dto.TransactionCancelDto;
import com.jh.accountmanagement.transaction.dto.TransactionCheckDto;
import com.jh.accountmanagement.transaction.dto.TransactionUseDto;
import com.jh.accountmanagement.transaction.exception.TransactionException;
import com.jh.accountmanagement.transaction.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transaction")
    public ResponseEntity<TransactionUseDto.Response> useMoney(@Valid @RequestBody TransactionUseDto.Request request) {
        try {
            Transaction transaction = transactionService.transactionUse(request);
            return ResponseEntity.ok(transaction.toUseResponse());
        } catch (TransactionException e) {
            log.error(e.getMessage());
            transactionService.useFail(request);
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/transaction")
    public ResponseEntity<TransactionCancelDto.Response> cancelMoney(@Valid @RequestBody TransactionCancelDto.Request request) {
        try {
            Transaction transaction = transactionService.canceledTransaction(request);
            return ResponseEntity.ok(transaction.toCancelResponse());
        } catch (TransactionException e) {
            log.error(e.getMessage());
            transactionService.cancelFail(request);
            throw e;
        }
    }

    @GetMapping("/transaction")
    public ResponseEntity<TransactionCheckDto.Response> check(@Valid @RequestParam @NotBlank @NotNull String transactionNumber) {
        Transaction transaction = transactionService.getTransaction(transactionNumber);
        return ResponseEntity.ok(transaction.toCheckResponse());
    }
}
