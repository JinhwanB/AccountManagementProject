package com.jh.accountmanagement.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class AccountCheck {
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
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    @ToString
    public static class Response {
        private long accountNum;
        private long money;
    }
}
