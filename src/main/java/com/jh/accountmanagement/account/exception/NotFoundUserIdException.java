package com.jh.accountmanagement.account.exception;

public class NotFoundUserIdException extends RuntimeException {
    public NotFoundUserIdException() {
        super();
    }

    public NotFoundUserIdException(String message) {
        super(message);
    }
}
