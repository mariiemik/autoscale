package com.example.order_service.dto;

import com.example.order_service.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Данные созданного заказа")
public record OrderResponseDTO(

        @Schema(description = "ID созданного заказа") String orderId,

        @Schema(description = "ID пользователя, который сделал заказ") String userId,

        @Schema(description = "Статут заказа") OrderStatus orderStatus,

        @Schema(description = "Список товаров в заказе") List<OrderItemDTO> items) {
}
