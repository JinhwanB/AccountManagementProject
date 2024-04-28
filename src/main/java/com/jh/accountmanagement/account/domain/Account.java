package com.jh.accountmanagement.account.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jh.accountmanagement.account.dto.AccountCheckDto;
import com.jh.accountmanagement.account.dto.AccountCreateDto;
import com.jh.accountmanagement.account.dto.AccountDeleteDto;
import com.jh.accountmanagement.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter // 테스트용
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "accountUser")
public class Account extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_user_id", nullable = false)
    private AccountUser accountUser;

    @Column(nullable = false)
    private long accountNum;

    @Column(nullable = false)
    private long money;

    @Column
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime delDate;

    // 계좌 생성시 Response로 변경 메소드
    public AccountCreateDto.Response toCreateResponse() {
        return AccountCreateDto.Response.builder()
                .accountNum(this.accountNum)
                .userId(this.accountUser.getUserId())
                .regDate(this.getRegDate())
                .build();
    }

    // 계좌 해지시 Response로 변경 메소드
    public AccountDeleteDto.Response toDeleteResponse() {
        return AccountDeleteDto.Response.builder()
                .accountNum(this.accountNum)
                .userId(this.accountUser.getUserId())
                .delDate(this.getDelDate())
                .build();
    }

    // 계좌 확인시 Response로 변경 메소드
    public AccountCheckDto.Response toCheckResponse() {
        return AccountCheckDto.Response.builder()
                .accountNum(this.accountNum)
                .money(this.money)
                .build();
    }
}
