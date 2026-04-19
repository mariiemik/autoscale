package com.example.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Детали заказа")
public record OrderDetailsDTO(@Schema(description = "ID заказа") String orderId,
                              @Schema(description = "ID пользователя") String userId,
                              @Schema(description = "имя пользователя") String userName,
                              @Schema(description = "статус оплаты") String paymentStatus,
                              @Schema(description = "список товаров") List<OrderItemDTO> items) {
}
