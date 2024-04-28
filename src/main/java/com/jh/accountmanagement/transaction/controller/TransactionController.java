package com.jh.accountmanagement.transaction.controller;

import com.jh.accountmanagement.config.RedisUtils;
import com.jh.accountmanagement.transaction.domain.Transaction;
import com.jh.accountmanagement.transaction.dto.TransactionCancelDto;
import com.jh.accountmanagement.transaction.dto.TransactionCheckDto;
import com.jh.accountmanagement.transaction.dto.TransactionRedisDto;
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
    private final RedisUtils redisUtils;

    @PostMapping("/transaction")
    public ResponseEntity<Object> useMoney(@Valid @RequestBody TransactionUseDto.Request request) {
        try {
            Transaction transaction = transactionService.transactionUse(request);
            return ResponseEntity.ok(transaction.toUseResponse());
        } catch (TransactionException e) {
            log.error(e.getMessage());
            transactionService.useFail(request);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/transaction")
    public ResponseEntity<Object> cancelMoney(@Valid @RequestBody TransactionCancelDto.Request request) {
        try {
            Transaction transaction = transactionService.canceledTransaction(request);
            return ResponseEntity.ok(transaction.toCancelResponse());
        } catch (TransactionException e) {
            log.error(e.getMessage());
            transactionService.cancelFail(request);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/transaction")
    public ResponseEntity<TransactionCheckDto.Response> check(@Valid @RequestParam @NotBlank @NotNull String transactionNumber) {
        if (redisUtils.hasKeyOfTransaction(transactionNumber)) {
            TransactionRedisDto transaction = redisUtils.getTransaction(transactionNumber);
            return ResponseEntity.ok(transaction.toCheckResponse());
        }

        Transaction transaction = transactionService.getTransaction(transactionNumber);
        redisUtils.setTransaction(transactionNumber, transaction.toRedisDto());
        return ResponseEntity.ok(transaction.toCheckResponse());
    }
}
