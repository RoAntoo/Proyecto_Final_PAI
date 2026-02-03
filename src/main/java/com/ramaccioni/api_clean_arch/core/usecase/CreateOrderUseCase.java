package com.ramaccioni.api_clean_arch.core.usecase;

import com.ramaccioni.api_clean_arch.core.dto.CreateOrderDTO;
import com.ramaccioni.api_clean_arch.core.exceptions.InvalidOrderAmountException;
import com.ramaccioni.api_clean_arch.core.exceptions.MissingRequiredFieldException;
import com.ramaccioni.api_clean_arch.core.exceptions.UserNotActiveException;
import com.ramaccioni.api_clean_arch.core.exceptions.UserNotFoundException;
import com.ramaccioni.api_clean_arch.core.input.ICreateOrderUseCaseInput;
import com.ramaccioni.api_clean_arch.core.model.Order;
import com.ramaccioni.api_clean_arch.core.model.User;
import java.math.RoundingMode;
import com.ramaccioni.api_clean_arch.core.output.IOrderRepository;
import com.ramaccioni.api_clean_arch.core.output.IUserRepository;

import java.math.BigDecimal;
import java.time.Clock;

public class CreateOrderUseCase  implements ICreateOrderUseCaseInput {
    private final IUserRepository userRepository;
    private final IOrderRepository orderRepository;
    private final Clock clock;

    public CreateOrderUseCase(IUserRepository userRepository, IOrderRepository orderRepository, Clock clock) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.clock = clock;
    }

    @Override
    public Order execute(CreateOrderDTO dto) {

        // Validaciones
        if (dto == null || dto.userId() == null || dto.amount() == null) {
            throw new MissingRequiredFieldException("User ID and Amount are required");
        }

        // Corregir formato decimales (borrar capaz??)
        BigDecimal amount = dto.amount().setScale(2, RoundingMode.HALF_UP);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOrderAmountException("Order amount must be greater than zero");
        }

        // Busca al usuario
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + dto.userId()));

        // ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡SOLO LOS USUARIOS ACTIVOS PUEDEN CREAR UNA ORDEN!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (!user.isActive()) {
            throw new UserNotActiveException("User is not ACTIVE. Current status: " + user.getStatus());
        }

        // Crear orden ----> usando Dominio ?¿ (buscar*)
        Order order = Order.create(user, amount, clock);

        // Almacena
        return orderRepository.save(order);
    }
}
