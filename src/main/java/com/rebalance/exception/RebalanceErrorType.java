package com.rebalance.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RebalanceErrorType {
    RB_999("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String description;
    private final HttpStatus status;
}
