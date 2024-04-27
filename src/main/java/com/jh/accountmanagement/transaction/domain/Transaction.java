package com.jh.accountmanagement.transaction.domain;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.global.BaseTimeEntity;
import com.jh.accountmanagement.transaction.dto.TransactionCancelDto;
import com.jh.accountmanagement.transaction.dto.TransactionUseDto;
import com.jh.accountmanagement.transaction.type.TransactionResult;
import com.jh.accountmanagement.transaction.type.TransactionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@ToString
public class Transaction extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_user_id", nullable = false)
    private AccountUser accountUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private long price;

    @Column(nullable = false)
    private String transactionNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionResult transactionResult;

    public TransactionUseDto.Response toUseResponse() {
        return TransactionUseDto.Response.builder()
                .accountNum(this.getAccount().getAccountNum())
                .regDate(this.getRegDate())
                .price(this.price)
                .transactionNumber(this.transactionNumber)
                .transactionResult(this.transactionResult.getMessage())
                .build();
    }

    public TransactionCancelDto.Response toCancelResponse(){
        return TransactionCancelDto.Response.builder()
                .accountNum(this.getAccount().getAccountNum())
                .transactionResult(this.getTransactionResult().getMessage())
                .transactionNumber(this.getTransactionNumber())
                .canceledPrice(this.getPrice())
                .transactionDate(this.getRegDate())
                .build();
    }
}
