package com.rebalance.exceptions;

public class FirstDateMustBeBeforeSecondDateException extends RuntimeException {

    private final static String DEFAULT_MESSAGE = "First date should be before or at the second date!";

    public FirstDateMustBeBeforeSecondDateException() {
        super(DEFAULT_MESSAGE);
    }

    public FirstDateMustBeBeforeSecondDateException(String message) {
        super(message);
    }
}
