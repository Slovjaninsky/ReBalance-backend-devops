package com.rebalance.exceptions;

public class InvalidRequestException extends RuntimeException{

    private static final String DEFAULT_MESSAGE = "Invalid request";

    public InvalidRequestException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidRequestException(String message) {
        super(message);
    }

}
