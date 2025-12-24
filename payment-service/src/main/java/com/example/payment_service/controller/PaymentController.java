package com.example.payment_service.controller;

import com.example.payment_service.dto.PaymentResponseDTO;
import com.example.payment_service.model.PaymentModel;
import com.example.payment_service.repository.PaymentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payments", description = "Управление оплатой")
@RequestMapping("/payment")
@RestController
public class PaymentController {

    private final static Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Operation(summary = "Детали платежа", description = "Возвращает детали платежа")
    @GetMapping("/{id}")
    public PaymentResponseDTO byOrder(@Parameter(description = "ID заказа", required = true)
                                      @PathVariable String orderId) {
        log.info("/payment/{}/ request", orderId);
        PaymentModel paymentModel = paymentRepository.findByOrderId(orderId);
        return new PaymentResponseDTO(paymentModel.getOrderId(), paymentModel.getPrice(), paymentModel.getStatus());
    }


}
