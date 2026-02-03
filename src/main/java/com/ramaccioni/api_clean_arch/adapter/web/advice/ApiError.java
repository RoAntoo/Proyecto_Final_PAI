package com.ramaccioni.api_clean_arch.adapter.web.advice;

import java.time.Instant;

public record ApiError(
        String code,
        String message,
        Instant timestamp
) {}
