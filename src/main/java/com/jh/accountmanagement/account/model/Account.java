package com.jh.accountmanagement.account.model;

import com.jh.accountmanagement.account.dto.AccountCreate;
import com.jh.accountmanagement.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "accountUser")
public class Account extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_user_id")
    private AccountUser accountUser;

    @Column(nullable = false)
    private long accountNum;

    @Column(nullable = false)
    private long money;

    @Column
    private LocalDateTime delDate;

    public AccountCreate.Response toResponse() {
        return AccountCreate.Response.builder()
                .accountNum(this.accountNum)
                .userId(this.accountUser.getUserId())
                .regDate(this.getRegDate())
                .build();
    }
}
