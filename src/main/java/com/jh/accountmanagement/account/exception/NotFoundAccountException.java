package com.jh.accountmanagement.account.exception;

public class NotFoundAccountException extends RuntimeException {
    public NotFoundAccountException() {
        super();
    }

    public NotFoundAccountException(String message) {
        super(message);
    }
}
