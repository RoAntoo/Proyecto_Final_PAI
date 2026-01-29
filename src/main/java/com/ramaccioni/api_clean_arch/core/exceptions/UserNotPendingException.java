package com.ramaccioni.api_clean_arch.core.exceptions;

public class UserNotPendingException extends RuntimeException{
    public UserNotPendingException(String message) {
        super(message);
    }
}
