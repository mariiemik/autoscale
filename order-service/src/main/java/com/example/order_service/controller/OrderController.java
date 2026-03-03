package com.example.order_service.controller;

import com.example.order_service.dto.OrderRequestDTO;
import com.example.order_service.dto.OrderResponseDTO;
import com.example.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Orders", description = "Управление заказами")
@RestController
@RequestMapping("/orders")
public class OrderController {

    OrderService orderService;
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Создать заказ", description = "Создает новый заказ для пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Заказ создан"),
            @ApiResponse(responseCode = "400", description = "Неправильное количество продукта"),
            @ApiResponse(responseCode = "409", description = "Недостаточно продукта на складе")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Valid OrderRequestDTO orderRequestDTO) {
        log.info("POST /orders — creating new order for user={}", orderRequestDTO.userId());

        OrderResponseDTO orderResponseDTO = orderService.createOrder(orderRequestDTO);
        log.info("/order request END");
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDTO);
    }

    @Operation(summary = "Получить заказ по ID", description = "Возвращает детали конкретного заказа")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ найден"),
            @ApiResponse(responseCode = "404", description = "Неверный id продукта"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(
            @Parameter(description = "ID заказа", required = true)
            @PathVariable String id) {
        log.info("/order/{id} request START");

        OrderResponseDTO response = orderService.getOrderById(id);
        log.info("/order/{id} request END");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Список всех заказов", description = "Возвращает список всех заказов, но без продуктов в заказе")
    @ApiResponse(responseCode = "200", description = "Список заказов получен")
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }


}
