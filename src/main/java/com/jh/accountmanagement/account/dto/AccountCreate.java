package com.jh.accountmanagement.account.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

public class AccountCreate {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    @ToString
    public static class Request {
        @NotNull
        @Min(1)
        private String userId;

        @NotNull
        @Min(100)
        private long initMoney;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    @ToString
    public static class Response {
        private String userId;
        private long accountNum;
        private LocalDateTime regDate;
    }
}
