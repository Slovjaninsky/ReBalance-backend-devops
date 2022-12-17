package com.example.databaseservice.exceptions;

public class IncorrectTimePeriodException extends RuntimeException {

    private final static String DEFAULT_MESSAGE = "Incorrect time period! Allowed types: 'day', 'week', 'month'";

    public IncorrectTimePeriodException() {
        super(DEFAULT_MESSAGE);
    }

    public IncorrectTimePeriodException(String message) {
        super(message);
    }
}
