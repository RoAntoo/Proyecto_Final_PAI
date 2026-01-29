package com.ramaccioni.api_clean_arch.core.exceptions;

public class ActivationExpiredException extends RuntimeException {
    public ActivationExpiredException(String message) {
        super(message);
    }
}
