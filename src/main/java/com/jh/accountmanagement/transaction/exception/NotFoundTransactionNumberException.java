package com.jh.accountmanagement.transaction.exception;

public class NotFoundTransactionNumberException extends RuntimeException {
    public NotFoundTransactionNumberException() {
        super();
    }

    public NotFoundTransactionNumberException(String message) {
        super(message);
    }
}
