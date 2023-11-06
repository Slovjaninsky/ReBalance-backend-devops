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
    RB_104("Amount of expense and user expenses is not the same", HttpStatus.CONFLICT),

    // groups
    RB_201("Group not found", HttpStatus.NOT_FOUND),
    RB_202("User not in group", HttpStatus.CONFLICT),
    RB_203("User already in group", HttpStatus.CONFLICT),
    RB_204("Group is not a personal group of user", HttpStatus.CONFLICT),
    RB_205("Group is personal", HttpStatus.CONFLICT),

    // images
    RB_301("Image not found", HttpStatus.NOT_FOUND),
    RB_302("Icon not found", HttpStatus.NOT_FOUND),

    // token
    RB_401("Invalid token", HttpStatus.BAD_REQUEST),
    RB_402("Bad credentials", HttpStatus.BAD_REQUEST);


    private final String description;
    private final HttpStatus status;
}
