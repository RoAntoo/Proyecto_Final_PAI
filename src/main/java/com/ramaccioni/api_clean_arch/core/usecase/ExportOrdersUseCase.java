package com.ramaccioni.api_clean_arch.core.usecase;

import com.ramaccioni.api_clean_arch.core.dto.ExportFileDTO;
import com.ramaccioni.api_clean_arch.core.dto.FindOrdersDTO;
import com.ramaccioni.api_clean_arch.core.exceptions.MissingRequiredFieldException;
import com.ramaccioni.api_clean_arch.core.model.Order;
import com.ramaccioni.api_clean_arch.core.output.IOrderRepository;
import com.ramaccioni.api_clean_arch.core.output.IOrdersExporter;
import com.ramaccioni.api_clean_arch.core.input.IExportOrdersUseCaseInput;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportOrdersUseCase implements IExportOrdersUseCaseInput {

    private final IOrderRepository orderRepository;
    private final IOrdersExporter exporter;
    private final Clock clock;

    public ExportOrdersUseCase(IOrderRepository orderRepository, IOrdersExporter exporter, Clock clock) {
        this.orderRepository = orderRepository;
        this.exporter = exporter;
        this.clock = clock;
    }

    @Override
    public ExportFileDTO execute(FindOrdersDTO dto) {
        if (dto == null) {
            throw new MissingRequiredFieldException("Filters dto is required");
        }

        List<Order> orders = orderRepository.search(dto.userId(), dto.status());
        byte[] content = exporter.export(orders);

        String ts = LocalDateTime.now(clock).format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String filename = "orders-" + ts + "." + exporter.fileExtension();

        return new ExportFileDTO(filename, exporter.contentType(), content);
    }
}
