package com.ramaccioni.api_clean_arch.core.exceptions;

public class UserAlreadyActiveException extends RuntimeException {
    public UserAlreadyActiveException(String message) {
        super(message);
    }
}
