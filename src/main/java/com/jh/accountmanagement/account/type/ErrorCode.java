package com.jh.accountmanagement.account.type;

public enum ErrorCode {
    NOT_FOUNT_USER_ID("해당 아이디의 사용자를 찾을 수 없습니다."),
    ACCOUNT_MAXIMUM("계좌는 최대 10개까지 만들 수 있습니다."),
    ALREADY_DELETED_ACCOUNT("이미 해지된 계좌입니다."),
    DELETE_ACCOUNT_FAIL("해당 계좌에 잔액이 남아있어 해지할 수 없습니다."),
    NOT_FOUND_ACCOUNT("해당 계좌번호는 없는 번호입니다.");
    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
