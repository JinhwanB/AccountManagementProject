package com.jh.accountmanagement.account.model;

import com.jh.accountmanagement.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class Account extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AccountUser accountUser;

    @Column(nullable = false)
    private long accountNum;

    @Column(nullable = false)
    private long money;

    @Column
    private LocalDateTime delDate;
}
