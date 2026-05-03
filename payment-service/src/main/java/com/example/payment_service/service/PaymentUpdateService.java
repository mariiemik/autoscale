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

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class PaymentUpdateService {
    private static final Logger log = LoggerFactory.getLogger(PaymentAsyncService.class);

    private void sendEvent(String topic, String key, Object event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);
        future.thenAccept(result -> {
            log.info("Событие {} успешно отправлено: key={}, partition={}", topic, key, result.getRecordMetadata().partition());
        }).exceptionally(ex -> {
            log.error("Ошибка при отправке события: key={}", key, ex);
            return null;
        });
    }

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentUpdateService(PaymentRepository paymentRepository,
                                KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }


    @Transactional
    public void updatePaymentStatus(String orderId, int totalPrice, PaymentResponse response) {

        Optional<PaymentModel> payment = paymentRepository.findByOrderId(orderId);

        if (payment.isEmpty()) {
            throw new IllegalStateException("Payment not found for orderId=" + orderId);
        }

        switch (response.paymentStatus()) {
            case SUCCESS -> {
                payment.get().setStatus(PaymentStatus.SUCCESS);
                sendEvent("payment-topic", orderId, new PaymentConfirmedEvent(orderId, totalPrice));
            }
            case FAIL -> {
                payment.get().setStatus(PaymentStatus.FAIL);
                sendEvent("payment-topic", orderId, new PaymentFailedEvent(orderId, totalPrice));
            }
        }

        paymentRepository.save(payment.get());

        log.info("payment updated: orderId={}, status={}", orderId, payment.get().getStatus());
    }
}
