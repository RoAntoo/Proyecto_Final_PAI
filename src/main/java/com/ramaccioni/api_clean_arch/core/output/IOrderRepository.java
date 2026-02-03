package com.ramaccioni.api_clean_arch.core.output;

import com.ramaccioni.api_clean_arch.core.enums.OrderStatus;
import com.ramaccioni.api_clean_arch.core.model.Order;

import java.util.List;

public interface IOrderRepository {
    Order save(Order order);

    List<Order> search(Long userId, OrderStatus status);
}
