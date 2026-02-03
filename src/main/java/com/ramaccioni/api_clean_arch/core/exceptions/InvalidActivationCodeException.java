package com.ramaccioni.api_clean_arch.core.exceptions;

public class InvalidActivationCodeException extends RuntimeException {
    public InvalidActivationCodeException(String message) {
        super(message);
    }
}
