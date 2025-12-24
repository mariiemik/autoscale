package com.example.notification_service.listener;

import com.example.common.enums.EventType;
import com.example.common.events.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationListener {
    public static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
            topics = {
                    "orders-topic",
                    "payment-topic",
                    "inventory-topic"
            },
            groupId = "notification-listener",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(Map<String, Object> rawEvent) throws JsonProcessingException {

        String eventTypeName = (String) rawEvent.get("type");
        EventType type = EventType.valueOf(eventTypeName);

        switch (type) {
//            case PAYMENT_CONFIRMED -> {
//                PaymentConfirmedEvent paymentConfirmedEvent = objectMapper.convertValue(rawEvent, PaymentConfirmedEvent.class);
//                log.info("");
//            }
            case ORDER_CREATED -> {
                OrderCreatedEvent orderCreatedEvent = objectMapper.convertValue(rawEvent, OrderCreatedEvent.class);
                log.info("Заказа с id={} пользователя {} сформирован", orderCreatedEvent.getOrderId(), orderCreatedEvent.getUserId());

            }
//            case ITEM_RESERVED -> {
//                ItemsReservedEvent itemsReservedEvent = objectMapper.convertValue(rawEvent, ItemsReservedEvent.class);
//
//            }
            case ORDER_CANCELLED -> {
                OrderCancelledEvent orderCancelledEvent = objectMapper.convertValue(rawEvent, OrderCancelledEvent.class);
                log.info("Заказа с id={} на сумму {} отменен из-за недостатка товаров на складе", orderCancelledEvent.getOrderId(), orderCancelledEvent.getPrice());

            }
            case ORDER_CONFIRMED -> {
                OrderConfirmedEvent orderConfirmedEvent = objectMapper.convertValue(rawEvent, OrderConfirmedEvent.class);
                log.info("Заказа с id={} на сумму {} подтвержден и оплачен", orderConfirmedEvent.getOrderId(), orderConfirmedEvent.getPrice());

            }
            case PAYMENT_FAILED -> {
                PaymentFailedEvent paymentFailedEvent = objectMapper.convertValue(rawEvent, PaymentFailedEvent.class);
                log.info("Не удалось произвести оплату для заказа с id={} на сумму {}", paymentFailedEvent.getOrderId(), paymentFailedEvent.getPrice());
            }
//            case ITEM_RESERVATION_CANCELLED -> {
//                ItemsReservationCancelledEvent itemsReservationCancelledEvent = objectMapper.convertValue(rawEvent, ItemsReservationCancelledEvent.class);
//            }


//            default -> log.info("[NOTIFICATION] Event {} received: {}", type, event);
        }


    }
}
