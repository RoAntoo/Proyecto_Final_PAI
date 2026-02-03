package com.ramaccioni.api_clean_arch.core.exceptions;

public class InvalidOrderAmountException extends RuntimeException {
    public InvalidOrderAmountException(String message) {
        super(message);
    }
}
