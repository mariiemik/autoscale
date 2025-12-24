package com.example.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "Товар в заказе")
public record OrderItemDTO(@Schema(description = "ID товара") String itemId,

                           @Schema(description = "Количество товара") int quantity,

                           @Schema(description = "Название товара") String name,

                           @Schema(description = "Стоимость товара") int price) {
}
