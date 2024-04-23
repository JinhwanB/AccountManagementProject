package com.jh.accountmanagement.account.model;

import com.jh.accountmanagement.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccountUser extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "accountUser", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Account> accountList = new ArrayList<>();

    @Column(nullable = false)
    private String userId;

    @Column
    private LocalDateTime delDate;
}
