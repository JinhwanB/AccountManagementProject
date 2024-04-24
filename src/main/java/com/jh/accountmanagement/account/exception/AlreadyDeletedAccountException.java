package com.jh.accountmanagement.account.exception;

public class AlreadyDeletedAccountException extends RuntimeException {
    public AlreadyDeletedAccountException() {
        super();
    }

    public AlreadyDeletedAccountException(String message) {
        super(message);
    }
}
