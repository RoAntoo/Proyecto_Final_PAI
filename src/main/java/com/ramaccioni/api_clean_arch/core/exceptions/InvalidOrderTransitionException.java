package com.ramaccioni.api_clean_arch.core.exceptions;

public class InvalidOrderTransitionException extends RuntimeException {
    public InvalidOrderTransitionException(String message) {
        super(message);
    }
}
