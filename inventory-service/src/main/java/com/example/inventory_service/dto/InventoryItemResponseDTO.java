package com.example.inventory_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Товар на складе")
public record InventoryItemResponseDTO(
        @Schema(description = "ID товара")
        String inventoryItemId,
        @Schema(description = "Имя товара")
        String name,
        @Schema(description = "Количество товара")
        int quantity,
        @Schema(description = "Цена товара")
        int price
) {
}
