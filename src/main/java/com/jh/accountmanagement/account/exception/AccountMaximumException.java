package com.jh.accountmanagement.account.exception;

public class AccountMaximumException extends RuntimeException {
    public AccountMaximumException() {
        super();
    }

    public AccountMaximumException(String message) {
        super(message);
    }
}
