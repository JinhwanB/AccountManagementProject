package com.jh.accountmanagement.transaction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

public class TransactionUseDto {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    @ToString
    public static class Request {
        @NotNull
        @NotBlank
        private String userId;

        @NotNull
        @NotBlank
        private String accountNum;

        @NotNull
        @Positive
        @Min(100)
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
        private long price;
        private LocalDateTime regDate;
    }
}
