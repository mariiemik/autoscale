package com.example.inventory_service.listener;

import com.example.common.enums.EventType;
import com.example.common.events.*;
import com.example.inventory_service.exception.InvalidItemQuantityException;
import com.example.inventory_service.service.InventoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class InventoryKafkaListener {
    private static final Logger log = LoggerFactory.getLogger(InventoryKafkaListener.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final InventoryService inventoryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    InventoryKafkaListener(InventoryService inventoryService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.inventoryService = inventoryService;
        this.kafkaTemplate = kafkaTemplate;
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

    @KafkaListener(topics = "orders-topic", groupId = "inventory-listener", containerFactory = "kafkaListenerContainerFactory")
    void listenOrders(Map<String, Object> rawEvent) throws JsonProcessingException {
        log.debug("got order event listenOrders() in inventory");
        try {
            String eventTypeName = (String) rawEvent.get("type");
            EventType type = EventType.valueOf(eventTypeName);
            switch (type) {
                case ORDER_CREATED -> {
                    OrderCreatedEvent orderCreatedEvent = objectMapper.convertValue(rawEvent, OrderCreatedEvent.class);

                    log.info("Начало обработки ORDER_CREATED: orderId={}", orderCreatedEvent.getOrderId());

                    try {
                        inventoryService.reserveOrderItems(orderCreatedEvent.getItems());
                        ItemsReservedEvent event = new ItemsReservedEvent(orderCreatedEvent.getOrderId(), orderCreatedEvent.getItems());
                        sendEvent("inventory-topic", orderCreatedEvent.getOrderId(), event);
                    } catch (InvalidItemQuantityException e) {
                        ItemsReservationCancelledEvent event = new ItemsReservationCancelledEvent(orderCreatedEvent.getOrderId(), orderCreatedEvent.getItems());
                        sendEvent("inventory-topic", event.getOrderId(), event);

                    }
                }
                case ORDER_CANCELLED -> {
                    OrderCancelledEvent orderCancelledEvent = objectMapper.convertValue(rawEvent, OrderCancelledEvent.class);
                    log.debug("Обработка события ORDER_CANCELLED");

                    log.info("Отмена резервирования для orderId={}", orderCancelledEvent.getOrderId());

                    for (OrderItemEvent itemEvent : orderCancelledEvent.getItems()) {
                        inventoryService.cancelItemReserve(itemEvent.getItemId(), itemEvent.getQuantity());
                    }

                }
                case ORDER_CONFIRMED -> {
                    OrderConfirmedEvent orderConfirmedEvent = objectMapper.convertValue(rawEvent, OrderConfirmedEvent.class);
                    log.debug("Обработка события ORDER_CONFIRMED");

                    log.info("Подтверждение заказа orderId={}", orderConfirmedEvent.getOrderId());

                    for (OrderItemEvent itemEvent : orderConfirmedEvent.getItems()) {
                        inventoryService.subtractBoughtItem(itemEvent.getItemId(), itemEvent.getQuantity());
                    }

                }

            }

        } catch (Exception e) {
            System.err.println("Ошибка парсинга или обработки: " + e.getMessage());
            log.error("Ошибка обработки события: {}", rawEvent, e);

        }

    }

}
