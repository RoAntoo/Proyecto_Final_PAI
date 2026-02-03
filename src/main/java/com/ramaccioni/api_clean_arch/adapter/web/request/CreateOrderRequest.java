package com.ramaccioni.api_clean_arch.adapter.web.request;

import java.math.BigDecimal;

public record CreateOrderRequest(
        BigDecimal amount) {
}
