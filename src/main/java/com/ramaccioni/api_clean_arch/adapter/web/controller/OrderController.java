package com.ramaccioni.api_clean_arch.adapter.web.controller;

import com.ramaccioni.api_clean_arch.adapter.web.request.CreateOrderRequest;
import com.ramaccioni.api_clean_arch.adapter.web.response.OrderResponse;
import com.ramaccioni.api_clean_arch.core.dto.CreateOrderDTO;
import com.ramaccioni.api_clean_arch.core.dto.ExportFileDTO;
import com.ramaccioni.api_clean_arch.core.dto.FindOrdersDTO;
import com.ramaccioni.api_clean_arch.core.enums.OrderStatus;
import com.ramaccioni.api_clean_arch.core.input.ICreateOrderUseCaseInput;
import com.ramaccioni.api_clean_arch.core.input.IExportOrdersUseCaseInput;
import com.ramaccioni.api_clean_arch.core.input.IFindOrdersUseCaseInput;
import com.ramaccioni.api_clean_arch.core.model.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    private final ICreateOrderUseCaseInput createOrder;
    private final IFindOrdersUseCaseInput findOrders;
    private final IExportOrdersUseCaseInput exportOrders;

    public OrderController(ICreateOrderUseCaseInput createOrder,
                           IFindOrdersUseCaseInput findOrders,
                           IExportOrdersUseCaseInput exportOrders) {
        this.createOrder = createOrder;
        this.findOrders = findOrders;
        this.exportOrders = exportOrders;
    }

    // POST /usuarios/{userId}/pedidos
    @PostMapping("/usuarios/{userId}/pedidos")
    public ResponseEntity<OrderResponse> createOrder(@PathVariable Long userId,
                                                     @RequestBody CreateOrderRequest request) {
        CreateOrderDTO dto = new CreateOrderDTO(userId, request.amount());
        Order created = createOrder.execute(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    // GET /orders?status=PENDING&userId=3
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> findOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long userId
    ) {
        FindOrdersDTO dto = new FindOrdersDTO(userId, status);
        List<Order> orders = findOrders.execute(dto);

        List<OrderResponse> response = orders.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    // GET /orders/export?status=PENDING&userId=3  -> descarga CSV
    @GetMapping("/orders/export")
    public ResponseEntity<byte[]> exportOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long userId
    ) {
        ExportFileDTO file = exportOrders.execute(new FindOrdersDTO(userId, status));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.filename() + "\"")
                .contentType(MediaType.parseMediaType(file.contentType()))
                .body(file.content());
    }

    private OrderResponse toResponse(Order o) {
        return new OrderResponse(
                o.getId(),
                o.getUser().getId(),
                o.getStatus(),
                o.getAmount(),
                o.getCreatedAt(),
                o.getUpdatedAt()
        );
    }
}
