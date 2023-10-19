package com.rebalance.exceptions;

public class BadDateException extends RuntimeException {

    private final static String DEFAULT_MESSAGE = "Date should be in format 'dd-mm-yyyy'";

    public BadDateException() {
        super(DEFAULT_MESSAGE);
    }

    public BadDateException(String message) {
        super(message);
    }

}
