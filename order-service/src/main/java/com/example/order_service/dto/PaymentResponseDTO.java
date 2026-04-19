package com.example.order_service.dto;

import com.example.common.dto.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация о платеже")
public record PaymentResponseDTO(
        @Schema(description = "Id заказа")
        String orderId,
        @Schema(description = "Стоимость заказа")
        int price,
        @Schema(description = "Статус платежа")
        PaymentStatus status) {
}
