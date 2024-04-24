package com.jh.accountmanagement.transaction.type;

import lombok.Getter;

@Getter
public enum TransactionType {
    TRANSACTION("잔액 사용"), CANCEL("잔액 사용 취소");
    private final String message;

    TransactionType(String message) {
        this.message = message;
    }
}
