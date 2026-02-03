package com.ramaccioni.api_clean_arch.adapter.web.advice;

import com.ramaccioni.api_clean_arch.core.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handle(UserNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handle(EmailAlreadyExistsException ex) {
        return build(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyActiveException.class)
    public ResponseEntity<ApiError> handle(UserAlreadyActiveException ex) {
        return build(HttpStatus.CONFLICT, "USER_ALREADY_ACTIVE", ex.getMessage());
    }

    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<ApiError> handle(UserNotActiveException ex) {
        return build(HttpStatus.BAD_REQUEST, "USER_NOT_ACTIVE", ex.getMessage());
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ApiError> handle(InvalidEmailException ex) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_EMAIL", ex.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiError> handle(InvalidPasswordException ex) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", ex.getMessage());
    }

    @ExceptionHandler(MissingRequiredFieldException.class)
    public ResponseEntity<ApiError> handle(MissingRequiredFieldException ex) {
        return build(HttpStatus.BAD_REQUEST, "MISSING_REQUIRED_FIELD", ex.getMessage());
    }

    @ExceptionHandler(InvalidOrderAmountException.class)
    public ResponseEntity<ApiError> handle(InvalidOrderAmountException ex) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_ORDER_AMOUNT", ex.getMessage());
    }

    @ExceptionHandler({
            UserNotPendingException.class,
            ActivationExpiredException.class,
            InvalidActivationCodeException.class,
            InvalidOrderTransitionException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handle(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.getMessage());
    }

    //
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                ex.getClass().getSimpleName() + ": " + ex.getMessage()
        );
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(new ApiError(code, message, Instant.now()));
    }
}
