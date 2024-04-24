package com.jh.accountmanagement.account.domain;

import com.jh.accountmanagement.account.dto.AccountCheck;
import com.jh.accountmanagement.account.dto.AccountCreate;
import com.jh.accountmanagement.account.dto.AccountDelete;
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
    private LocalDateTime delDate;

    // 계좌 생성시 Response로 변경 메소드
    public AccountCreate.Response toCreateResponse() {
        return AccountCreate.Response.builder()
                .accountNum(this.accountNum)
                .userId(this.accountUser.getUserId())
                .regDate(this.getRegDate())
                .build();
    }

    // 계좌 해지시 Response로 변경 메소드
    public AccountDelete.Response toDeleteResponse() {
        return AccountDelete.Response.builder()
                .accountNum(this.accountNum)
                .userId(this.accountUser.getUserId())
                .delDate(this.getDelDate())
                .build();
    }

    // 계좌 확인시 Response로 변경 메소드
    public AccountCheck.Response toCheckResponse() {
        return AccountCheck.Response.builder()
                .accountNum(this.accountNum)
                .money(this.money)
                .build();
    }
}
