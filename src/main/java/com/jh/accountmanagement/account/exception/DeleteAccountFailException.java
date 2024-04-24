package com.jh.accountmanagement.account.exception;

public class DeleteAccountFailException extends RuntimeException {
    public DeleteAccountFailException() {
        super();
    }

    public DeleteAccountFailException(String message) {
        super(message);
    }
}
