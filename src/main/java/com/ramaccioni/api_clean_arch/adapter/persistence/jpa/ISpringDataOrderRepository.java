package com.ramaccioni.api_clean_arch.adapter.persistence.jpa;

import com.ramaccioni.api_clean_arch.core.enums.OrderStatus;
import com.ramaccioni.api_clean_arch.core.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ISpringDataOrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_Id(Long userId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByUser_IdAndStatus(Long userId, OrderStatus status);
}
