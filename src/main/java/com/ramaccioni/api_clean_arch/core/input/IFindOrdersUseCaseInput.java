package com.ramaccioni.api_clean_arch.core.input;

import com.ramaccioni.api_clean_arch.core.dto.FindOrdersDTO;
import com.ramaccioni.api_clean_arch.core.model.Order;

import java.util.List;

public interface IFindOrdersUseCaseInput {
    List<Order> execute(FindOrdersDTO dto);
}
