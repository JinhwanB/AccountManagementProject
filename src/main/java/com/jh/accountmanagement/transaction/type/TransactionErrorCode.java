package com.jh.accountmanagement.transaction.type;

import lombok.Getter;

@Getter
public enum TransactionErrorCode {
    NOT_FOUND_TRANSACTION_NUMBER("해당 거래 번호에 해당하는 거래 내용이 없습니다."),
    NOT_FOUND_ACCOUNT_NUMBER_BY_TRANSACTION("해당 거래를 진행한 계좌번호가 아닙니다."),
    PRICE_MORE_THAN_ACCOUNT_MONEY("거래 금액이 계좌 잔액을 초과한 금액입니다."),
    ALREADY_CANCELED_TRANSACTION("이미 취소 완료되었습니다."),
    DIFF_PRICE_AND_ACCOUNT_MONEY("원거래 금액과 취소 금액이 다릅니다");
    private final String message;

    TransactionErrorCode(String message) {
        this.message = message;
    }
}
