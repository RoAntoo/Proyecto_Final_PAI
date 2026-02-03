package com.ramaccioni.api_clean_arch.core.dto;

public record ActivateUserDTO(
        Long userId,
        String activationCode) {
}
