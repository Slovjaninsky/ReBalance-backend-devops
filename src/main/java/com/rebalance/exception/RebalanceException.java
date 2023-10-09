package com.rebalance.exception;

import lombok.Getter;

@Getter
public class RebalanceException extends RuntimeException {
    private final RebalanceErrorType errorType;


    public RebalanceException(RebalanceErrorType errorType) {
        super(errorType.name());
        this.errorType = errorType;
    }
}
