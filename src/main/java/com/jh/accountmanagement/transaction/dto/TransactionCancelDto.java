package com.jh.accountmanagement.transaction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

public class TransactionCancelDto {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    @ToString
    static class Request {
        @NotNull
        @NotBlank
        private String transactionNumber;

        @NotNull
        @Positive
        private long accountNum;

        @NotNull
        @Min(100)
        @Positive
        private long price;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    @ToString
    static class Response {
        private long accountNum;
        private String transactionResult;
        private String transactionNumber;
        private long canceledPrice;
        private LocalDateTime transactionDate;
    }
}
