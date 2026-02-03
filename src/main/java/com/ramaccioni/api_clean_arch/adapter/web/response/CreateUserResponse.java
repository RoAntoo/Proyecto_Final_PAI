package com.ramaccioni.api_clean_arch.adapter.web.response;

import com.ramaccioni.api_clean_arch.core.enums.UserStatus;

import java.time.LocalDateTime;

public record CreateUserResponse(
        Long id,
        String email,
        UserStatus status,
        String activationCode,
        LocalDateTime activationExpiresAt
) {}
