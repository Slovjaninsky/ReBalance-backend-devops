package com.example.databaseservice.exceptions;

public class GlobalIdTakenException extends RuntimeException{

    private static final String DEFAULT_MESSAGE = "GlobalId taken";

    public GlobalIdTakenException() {
        super(DEFAULT_MESSAGE);
    }

    public GlobalIdTakenException(String message) {
        super(message);
    }

}
