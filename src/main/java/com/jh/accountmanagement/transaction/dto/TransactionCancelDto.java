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
    public static class Request {
        @NotNull
        @NotBlank
        private String transactionNumber;

        @NotNull
        @NotBlank
        private String accountNum;

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
    public static class Response {
        private String accountNum;
        private String transactionResult;
        private String transactionNumber;
        private long canceledPrice;
        private LocalDateTime transactionDate;
    }
}
