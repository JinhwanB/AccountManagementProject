package com.jh.accountmanagement.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

public class TransactionCheckDto {
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    @ToString
    public static class Request {
        @NotNull
        @NotBlank
        private String transactionNumber;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    @ToString
    public static class Response {
        private String accountNum;
        private String transactionType;
        private String transactionResult;
        private String transactionNumber;
        private long transactionPrice;
        private LocalDateTime transactionDate;
    }
}
