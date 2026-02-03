package com.ramaccioni.api_clean_arch.core.dto;

import com.ramaccioni.api_clean_arch.core.enums.UserStatus;

public record UserResponseDTO(
        Long id,
        String email,
        UserStatus status
) {
}
