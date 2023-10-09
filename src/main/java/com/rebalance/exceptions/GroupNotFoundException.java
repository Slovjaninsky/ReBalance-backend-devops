package com.rebalance.exceptions;

public class GroupNotFoundException extends RuntimeException{

    private static final String DEFAULT_MESSAGE = "Group not found";

    public GroupNotFoundException() {
    }

    public GroupNotFoundException(String message) {
        super(message);
    }

}
