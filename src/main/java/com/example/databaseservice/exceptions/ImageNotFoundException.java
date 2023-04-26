package com.example.databaseservice.exceptions;

public class ImageNotFoundException extends RuntimeException{

    private static final String MESSAGE = "Image not found!";
    public ImageNotFoundException() {
        super(MESSAGE);
    }

    public ImageNotFoundException(String message) {
        super(message);
    }
}
