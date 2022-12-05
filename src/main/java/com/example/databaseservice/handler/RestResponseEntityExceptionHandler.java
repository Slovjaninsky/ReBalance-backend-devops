package com.example.databaseservice.handler;

import com.example.databaseservice.exceptions.EmailTakenException;
import com.example.databaseservice.exceptions.ExpenseNotFoundException;
import com.example.databaseservice.exceptions.GroupNotFoundException;
import com.example.databaseservice.exceptions.InvalidRequestException;
import com.example.databaseservice.exceptions.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value =
            {
                    EmailTakenException.class,
                    ExpenseNotFoundException.class,
                    GroupNotFoundException.class,
                    InvalidRequestException.class,
                    UserNotFoundException.class
            }
    )
    protected ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request
    ) {
        return handleExceptionInternal(
                ex, ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, request
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request
    ) {
        return handleExceptionInternal(
                ex, Objects.requireNonNull(ex.getFieldError()).getDefaultMessage(), headers, status, request
        );
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request
    ) {
        return handleExceptionInternal(
                ex,
                ex.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("\n")),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }
}