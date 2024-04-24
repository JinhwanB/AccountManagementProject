package com.jh.accountmanagement.transaction.type;

import lombok.Getter;

@Getter
public enum TransactionResult {
    S("성공"), F("실패");
    private final String message;

    TransactionResult(String message) {
        this.message = message;
    }
}
