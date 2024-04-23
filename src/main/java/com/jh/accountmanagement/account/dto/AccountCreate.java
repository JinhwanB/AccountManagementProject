package com.jh.accountmanagement.account.dto;

import lombok.*;

import java.time.LocalDateTime;

public class AccountCreate {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    @ToString
    public static class Request {
        private String userId;
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
