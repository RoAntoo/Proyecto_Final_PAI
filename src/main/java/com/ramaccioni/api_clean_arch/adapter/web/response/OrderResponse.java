package com.ramaccioni.api_clean_arch.adapter.web.response;

import com.ramaccioni.api_clean_arch.core.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Long userId,
        OrderStatus status,
        BigDecimal amount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
