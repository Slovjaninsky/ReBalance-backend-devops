package com.rebalance.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

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

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        RebalanceErrorType errorType = RebalanceErrorType.RB_998;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                errorType.getStatus(),
                errorType.getDescription() + errors);
        problemDetail.setTitle(errorType.toString());

        return ResponseEntity.status(errorType.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleRuntimeException(Exception ex) {
        return handleRebalanceException(new RebalanceException(RebalanceErrorType.RB_999));
    }
}
