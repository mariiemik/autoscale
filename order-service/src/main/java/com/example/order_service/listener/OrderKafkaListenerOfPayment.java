package com.example.order_service.listener;

import com.example.common.enums.EventType;
import com.example.common.events.*;
import com.example.order_service.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component

public class OrderKafkaListenerOfPayment {
    private static final Logger log = LoggerFactory.getLogger(OrderKafkaListenerOfPayment.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Timer eventProcessingTimer;

    OrderKafkaListenerOfPayment(OrderService orderService,
                                KafkaTemplate<String, Object> kafkaTemplate, MeterRegistry registry) {
        this.orderService = orderService;
        this.kafkaTemplate = kafkaTemplate;
        this.eventProcessingTimer = Timer.builder("event_processing_seconds")
                .description("Time to process one Kafka event")
                .tag("service", "order-service")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
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

    @KafkaListener(topics = "payment-topic",
            groupId = "order-listener",
            containerFactory = "kafkaListenerContainerFactory"
    )
    void listenPayments(Map<String, Object> rawEvent) throws JsonProcessingException {
        try {

            String eventTypeName = (String) rawEvent.get("type");
            EventType type = EventType.valueOf(eventTypeName);
            eventProcessingTimer.record(() -> {
                switch (type) {
                    case PAYMENT_CONFIRMED -> {
                        PaymentConfirmedEvent paymentConfirmedEvent = objectMapper.convertValue(rawEvent, PaymentConfirmedEvent.class);
                        log.debug("обработка события PAYMENT_CONFIRMED ");

                        String userId = orderService.confirmOrder(paymentConfirmedEvent.getOrderId());
                        OrderConfirmedEvent event = new OrderConfirmedEvent(paymentConfirmedEvent.getOrderId(), userId, paymentConfirmedEvent.getPrice(), orderService.getOrderById(paymentConfirmedEvent.getOrderId()).items().stream().map(x -> new OrderItemEvent(x.itemId(), x.quantity(), x.price())).toList());
                        log.debug("event = {} eventType = {}", event, event.getType());
                        sendEvent("orders-topic", event.getOrderId(), event);
                        //TODO notification

                    }
                    case PAYMENT_FAILED -> {
                        PaymentFailedEvent paymentFailedEvent = objectMapper.convertValue(rawEvent, PaymentFailedEvent.class);
                        log.debug("обработка события PAYMENT_FAILED ");

                        String userId = orderService.cancelOrder(paymentFailedEvent.getOrderId());

                        OrderCancelledEvent event = new OrderCancelledEvent(paymentFailedEvent.getOrderId(), userId, paymentFailedEvent.getPrice(), orderService.getOrderById(paymentFailedEvent.getOrderId()).items().stream().map(x -> new OrderItemEvent(x.itemId(), x.quantity(), x.price())).toList());
                        log.debug("event = {}, eventType = {}", event, event.getType());

                        sendEvent("orders-topic", event.getOrderId(), event);
                        //TODO notification

                    }

                }
            });

        } catch (Exception e) {
            System.err.println("Ошибка парсинга или обработки: " + e.getMessage());
        }

    }

}
