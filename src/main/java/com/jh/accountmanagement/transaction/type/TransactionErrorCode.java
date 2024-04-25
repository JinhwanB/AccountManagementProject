package com.jh.accountmanagement.transaction.type;

import lombok.Getter;

@Getter
public enum TransactionErrorCode {
    NOT_FOUND_TRANSACTION_NUMBER("해당 거래 번호에 해당하는 계좌번호와 다릅니다."),
    PRICE_MORE_THAN_ACCOUNT_MONEY("거래 금액이 계좌 잔액을 초과한 금액입니다."),
    DIFF_PRICE_AND_ACCOUNT_MONEY("원거래 금액과 취소 금액이 다릅니다");
    private final String message;

    TransactionErrorCode(String message) {
        this.message = message;
    }
}
