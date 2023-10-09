package com.rebalance.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RebalanceErrorType {
    RB_999("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    RB_998("Incorrect request data: ", HttpStatus.BAD_REQUEST),

    // users
    RB_001("Email already taken", HttpStatus.CONFLICT),
    RB_002("User not found", HttpStatus.NOT_FOUND),

    // expenses
    RB_101("Expense not found", HttpStatus.NOT_FOUND),
    RB_102("First date should be before or at the second date", HttpStatus.CONFLICT),
    RB_103("Incorrect time period. Allowed types: 'day', 'week', 'month', 'year'", HttpStatus.CONFLICT),

    // groups
    RB_201("Group not found", HttpStatus.NOT_FOUND),

    // images
    RB_301("Image not found", HttpStatus.NOT_FOUND),
    RB_302("Icon not found", HttpStatus.NOT_FOUND);


    private final String description;
    private final HttpStatus status;
}
