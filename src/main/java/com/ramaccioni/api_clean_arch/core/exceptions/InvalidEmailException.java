package com.ramaccioni.api_clean_arch.core.exceptions;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
