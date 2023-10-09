package com.rebalance.exceptions;

public class ExpenseNotFoundException extends RuntimeException{

    private static final String DEFAULT_MESSAGE = "Expense not found";

    public ExpenseNotFoundException() {
    }

    public ExpenseNotFoundException(String message) {
        super(message);
    }

}
