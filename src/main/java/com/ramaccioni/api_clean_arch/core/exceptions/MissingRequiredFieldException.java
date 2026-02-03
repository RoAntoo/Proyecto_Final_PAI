package com.ramaccioni.api_clean_arch.core.exceptions;

public class MissingRequiredFieldException extends RuntimeException {
    public MissingRequiredFieldException(String message) {
        super(message);
    }
}
