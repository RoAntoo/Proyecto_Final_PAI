package com.ramaccioni.api_clean_arch.core.dto;

import com.ramaccioni.api_clean_arch.core.enums.OrderStatus;

public record FindOrdersDTO(
        Long userId,
        OrderStatus status
) {
}
