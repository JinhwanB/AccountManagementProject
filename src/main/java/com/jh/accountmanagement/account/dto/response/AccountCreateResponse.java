package com.jh.accountmanagement.account.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@ToString
public class AccountCreateResponse {
    private String userId;
    private long accountNum;
    private LocalDateTime regDate;
}
