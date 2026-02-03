package com.ramaccioni.api_clean_arch.core.model;

import com.ramaccioni.api_clean_arch.core.enums.OrderStatus;
import com.ramaccioni.api_clean_arch.core.exceptions.InvalidOrderAmountException;
import com.ramaccioni.api_clean_arch.core.exceptions.InvalidOrderTransitionException;
import com.ramaccioni.api_clean_arch.core.exceptions.MissingRequiredFieldException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString(exclude = {"user"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public static Order create(User user, BigDecimal amount, Clock clock)
    {
        if (user == null) throw new MissingRequiredFieldException("user is required");
        if (amount == null) throw new MissingRequiredFieldException("amount is required");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new InvalidOrderAmountException("amount must be > 0");
        if (clock == null) throw new MissingRequiredFieldException("clock is required");

        LocalDateTime now = LocalDateTime.now(clock);

        Order order = new Order();
        order.user = user;
        order.amount = amount;
        order.status = OrderStatus.PENDING;
        order.createdAt = now;
        order.updatedAt = now;

        return order;
    }

    public void transitionTo(OrderStatus newStatus, Clock clock) {
        if (!isValidTransition(this.status, newStatus)) {
            throw new InvalidOrderTransitionException("Cannot transition from " + this.status + " to " + newStatus);
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now(clock);
    }

    private boolean isValidTransition(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case PENDING    -> to == OrderStatus.PROCESSING || to == OrderStatus.CANCELLED;
            case PROCESSING -> to == OrderStatus.APPROVED   || to == OrderStatus.REJECTED;
            default         -> false;
        };
    }

    public void startProcessing(Clock clock) {
        transitionTo(OrderStatus.PROCESSING, clock);
    }

    public void approve(Clock clock) {
        transitionTo(OrderStatus.APPROVED, clock);
    }

    public void reject(Clock clock) {
        transitionTo(OrderStatus.REJECTED, clock);
    }

    public void cancel(Clock clock) {
        transitionTo(OrderStatus.CANCELLED, clock);
    }
}