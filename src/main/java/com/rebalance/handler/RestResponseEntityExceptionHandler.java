package com.rebalance.handler;

import com.rebalance.exceptions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value =
            {
                    EmailTakenException.class,
                    InvalidRequestException.class,
                    BadDateException.class,
                    FirstDateMustBeBeforeSecondDateException.class,
                    IncorrectTimePeriodException.class
            }
    )
    protected ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request
    ) {
        return handleExceptionInternal(
                ex, ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, request
        );
    }

    @ExceptionHandler(value =
            {
                    ExpenseNotFoundException.class,
                    GroupNotFoundException.class,
                    ImageNotFoundException.class,
                    UserNotFoundException.class
            }
    )
    protected ResponseEntity<Object> handleNotFound(
            RuntimeException ex, WebRequest request
    ) {
        return handleExceptionInternal(
                ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request
        );
    }
}