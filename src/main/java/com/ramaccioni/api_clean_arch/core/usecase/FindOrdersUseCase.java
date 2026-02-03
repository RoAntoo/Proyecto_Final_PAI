package com.ramaccioni.api_clean_arch.core.usecase;

import com.ramaccioni.api_clean_arch.core.dto.FindOrdersDTO;
import com.ramaccioni.api_clean_arch.core.exceptions.MissingRequiredFieldException;
import com.ramaccioni.api_clean_arch.core.input.IFindOrdersUseCaseInput;
import com.ramaccioni.api_clean_arch.core.model.Order;
import com.ramaccioni.api_clean_arch.core.output.IOrderRepository;

import java.util.List;

public class FindOrdersUseCase implements IFindOrdersUseCaseInput {
    private final IOrderRepository orderRepository;

    public FindOrdersUseCase(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> execute(FindOrdersDTO dto) {

        // Si el dto no trae nada, devolvemos la lista vacia
        if (dto == null) {
            throw new MissingRequiredFieldException("Filters are required");
        }

        // Logica y trabajo del repo
        return orderRepository.search(dto.userId(), dto.status());
    }
}
