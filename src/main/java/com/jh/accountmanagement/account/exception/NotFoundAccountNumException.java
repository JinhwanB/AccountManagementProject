package com.jh.accountmanagement.account.exception;

public class NotFoundAccountNumException extends RuntimeException {
    public NotFoundAccountNumException() {
        super();
    }

    public NotFoundAccountNumException(String message) {
        super(message);
    }
}
