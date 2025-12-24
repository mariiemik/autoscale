package com.example.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "Данные для создания заказа")
public record OrderRequestDTO(
        @NotBlank @Schema(description = "ID пользователя, который делает заказ")
        String userId,
        @Schema(description = "Список товаров в заказе") List<OrderItemDTO> items) {
}
