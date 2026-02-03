package com.ramaccioni.api_clean_arch.core.input;

import com.ramaccioni.api_clean_arch.core.dto.CreateOrderDTO;
import com.ramaccioni.api_clean_arch.core.model.Order;

public interface ICreateOrderUseCaseInput {
    Order execute(CreateOrderDTO dto);
}
