package com.example.databaseservice.handler;

import com.example.databaseservice.exceptions.*;
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

//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex, HttpHeaders headers,
//            HttpStatus status, WebRequest request
//    ) {
//        return handleExceptionInternal(
//                ex, Objects.requireNonNull(ex.getFieldError()).getDefaultMessage(), headers, status, request
//        );
//    }

//    @ExceptionHandler(value = ConstraintViolationException.class)
//    protected ResponseEntity<Object> handleConstraintViolationException(
//            ConstraintViolationException ex, WebRequest request
//    ) {
//        return handleExceptionInternal(
//                ex,
//                ex.getConstraintViolations().stream()
//                        .map(ConstraintViolation::getMessage)
//                        .collect(Collectors.joining("\n")),
//                new HttpHeaders(),
//                HttpStatus.BAD_REQUEST,
//                request
//        );
//    }
//
//    @ExceptionHandler(value = RuntimeException.class)
//    protected ResponseEntity<Object> handleRuntimeExceptions(
//            ConstraintViolationException ex, WebRequest request
//    ) {
//        return handleExceptionInternal(
//                ex,
//                ex.getConstraintViolations().stream()
//                        .map(ConstraintViolation::getMessage)
//                        .collect(Collectors.joining("\n")),
//                new HttpHeaders(),
//                HttpStatus.INTERNAL_SERVER_ERROR,
//                request
//        );
//    }

}