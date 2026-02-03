package com.ramaccioni.api_clean_arch.core.dto;

import java.math.BigDecimal;

public record CreateOrderDTO(
        Long userId,
        BigDecimal amount
){}
