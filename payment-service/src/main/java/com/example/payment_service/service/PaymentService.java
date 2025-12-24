package com.example.payment_service.service;

import com.example.common.dto.PaymentResponse;
import com.example.common.dto.PaymentStatus;
import com.example.common.events.PaymentConfirmedEvent;
import com.example.common.events.PaymentFailedEvent;
import com.example.payment_service.model.PaymentModel;
import com.example.payment_service.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

@Service
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WebClient paymentWebClient; // 1. Объявление
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository, KafkaTemplate<String, Object> kafkaTemplate, WebClient paymentWebClient) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.paymentWebClient = paymentWebClient;
    }

    private void sendEvent(String topic, String key, Object event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);
        future.thenAccept(result -> {
            log.info("Событие {} успешно отправлено: key={}, partition={}", topic, key, result.getRecordMetadata().partition());
        }).exceptionally(ex -> {
            log.error("Ошибка при отправке события: key={}", key, ex);
            return null;
        });
    }

    @Transactional
    public PaymentResponse payForOrder(String orderId, int totalPrice) {
        log.info("start of payForOrder() method in PaymentService");
        PaymentResponse paymentResponse = paymentWebClient.post()
                .uri("/external_payment")
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block();
        PaymentModel paymentModel = new PaymentModel(orderId, totalPrice, PaymentStatus.IN_PROGRESS);
        paymentRepository.save(paymentModel);
        log.debug("{}", paymentResponse.paymentStatus());
        switch (paymentResponse.paymentStatus()) {
            case SUCCESS -> {
                log.info("payment success");
                PaymentConfirmedEvent event = new PaymentConfirmedEvent(orderId, totalPrice);
                sendEvent("payment-topic", orderId, event);
                paymentModel.setStatus(PaymentStatus.SUCCESS);
            }
            case FAIL -> {
                log.info("payment fail");
                PaymentFailedEvent event = new PaymentFailedEvent(orderId, totalPrice);
                sendEvent("payment-topic", orderId, event);
                paymentModel.setStatus(PaymentStatus.FAIL);
            }
        }
        paymentRepository.save(paymentModel);
        log.info("end of payForOrder() method in PaymentService");

        return paymentResponse;
    }
}
