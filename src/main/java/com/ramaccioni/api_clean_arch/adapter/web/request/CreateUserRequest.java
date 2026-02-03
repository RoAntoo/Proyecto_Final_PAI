package com.ramaccioni.api_clean_arch.adapter.web.request;

public record CreateUserRequest(
        String email,
        String password) {
}
