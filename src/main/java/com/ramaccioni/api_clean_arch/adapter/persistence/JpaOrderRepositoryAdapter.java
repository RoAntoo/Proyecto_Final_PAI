package com.ramaccioni.api_clean_arch.adapter.persistence;

import com.ramaccioni.api_clean_arch.adapter.persistence.jpa.ISpringDataOrderRepository;
import com.ramaccioni.api_clean_arch.core.enums.OrderStatus;
import com.ramaccioni.api_clean_arch.core.model.Order;
import com.ramaccioni.api_clean_arch.core.output.IOrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaOrderRepositoryAdapter implements IOrderRepository {

    private final ISpringDataOrderRepository jpa;

    public JpaOrderRepositoryAdapter(ISpringDataOrderRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Order save(Order order) {
        return jpa.save(order);
    }

    @Override
    public List<Order> search(Long userId, OrderStatus status) {
        if (userId == null && status == null) {
            return jpa.findAll();
        }
        if (userId != null && status == null) {
            return jpa.findByUser_Id(userId);
        }
        if (userId == null) { // status != null
            return jpa.findByStatus(status);
        }
        return jpa.findByUser_IdAndStatus(userId, status);
    }
}
