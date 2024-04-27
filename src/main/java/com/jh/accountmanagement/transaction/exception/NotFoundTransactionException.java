package com.jh.accountmanagement.transaction.exception;

public class NotFoundTransactionException extends RuntimeException{
    public NotFoundTransactionException(){
        super();
    }

    public NotFoundTransactionException(String message){
        super(message);
    }
}
