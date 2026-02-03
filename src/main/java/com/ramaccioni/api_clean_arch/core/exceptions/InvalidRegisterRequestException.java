package com.ramaccioni.api_clean_arch.core.exceptions;

public class InvalidRegisterRequestException extends RuntimeException {
    public InvalidRegisterRequestException(String message) {
        super(message);
    }
}
