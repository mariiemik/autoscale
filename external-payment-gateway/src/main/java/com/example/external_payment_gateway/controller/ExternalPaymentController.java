package com.example.external_payment_gateway.controller;

import com.example.common.dto.PaymentResponse;
import com.example.common.dto.PaymentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/external_payment")
@Tag(name = "External payment service", description = "Имитация внешней платёжной системы")

public class ExternalPaymentController {
    @Value("${payment.delay.min:200}")
    int minDelay;
    @Value("${payment.delay.max:800}")
    int maxDelay;
    @Value("${payment.fail.probability:0.1}")
    double failProbability;

    private final Random random = new Random();
    private static final Logger log = LoggerFactory.getLogger(ExternalPaymentController.class);

    @PostMapping
    @Operation(summary = "Начать оплату", description = "Имитирует процесс оплаты: задержка 200–800 ms и 0.1 вероятность провала")
    public PaymentResponse pay() throws InterruptedException {
        // delay 200–800 ms
        int delay = minDelay + random.nextInt(maxDelay - minDelay + 1);
        boolean failed = random.nextDouble() < failProbability;

        log.info("Payment request received. Delay={}ms, Result={}", delay, failed ? "FAIL" : "SUCCESS");

        Thread.sleep(delay);

        return new PaymentResponse(
                failed ? PaymentStatus.FAIL : PaymentStatus.SUCCESS
        );
    }
}
