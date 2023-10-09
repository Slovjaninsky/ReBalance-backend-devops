package com.rebalance.exceptions;

public class EmailTakenException extends RuntimeException{

    private static final String DEFAULT_MESSAGE = "Email already taken";

    public EmailTakenException() {
    }

    public EmailTakenException(String message) {
        super(message);
    }

}
