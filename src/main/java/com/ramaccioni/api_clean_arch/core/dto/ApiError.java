package com.ramaccioni.api_clean_arch.core.dto;

import java.time.Instant;

public record ApiError(
        String code,
        String message,
        Instant timestamp
) {}
