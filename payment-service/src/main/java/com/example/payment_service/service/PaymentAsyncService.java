package com.example.payment_service.service;

import com.example.common.dto.PaymentResponse;
import com.example.common.dto.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PaymentAsyncService {
    private static final Logger log = LoggerFactory.getLogger(PaymentAsyncService.class);
    private final WebClient paymentWebClient; // 1. Объявление
    private final PaymentUpdateService paymentUpdateService;

    public PaymentAsyncService(WebClient paymentWebClient, PaymentUpdateService paymentUpdateService) {
        this.paymentWebClient = paymentWebClient;
        this.paymentUpdateService = paymentUpdateService;
    }

    @Async
    @Transactional
    public void processPaymentAsync(String orderId, int totalPrice) {
        log.info("start async payment for orderId={}", orderId);

        try {
            PaymentResponse response = paymentWebClient.post()
                    .uri("/external_payment")
                    .retrieve()
                    .bodyToMono(PaymentResponse.class)
                    .block();

            paymentUpdateService.updatePaymentStatus(orderId, totalPrice, response);

        } catch (Exception e) {
            log.error("payment failed HARD for orderId={}", orderId, e);
            paymentUpdateService.updatePaymentStatus(orderId, totalPrice, new PaymentResponse(PaymentStatus.FAIL));
        }
    }
}
