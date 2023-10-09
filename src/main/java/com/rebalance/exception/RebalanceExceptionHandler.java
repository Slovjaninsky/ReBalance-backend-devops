package com.rebalance.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@AllArgsConstructor
@ControllerAdvice
public class RebalanceExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(RebalanceException.class)
    ProblemDetail handleRebalanceException(RebalanceException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                e.getErrorType().getStatus(),
                e.getErrorType().getDescription());
        problemDetail.setTitle(e.getErrorType().toString());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleRuntimeException(Exception e) {
        return handleRebalanceException(new RebalanceException(RebalanceErrorType.RB_999));
    }
}
