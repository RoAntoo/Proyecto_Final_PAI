package com.ramaccioni.api_clean_arch.core.exceptions;

public class UserNotActiveException extends RuntimeException {
    public UserNotActiveException(String message) {
        super(message);
    }
}
