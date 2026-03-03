package com.example.order_service.listener;

import com.example.common.enums.EventType;
import com.example.common.events.ItemsReservationCancelledEvent;
import com.example.order_service.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component

public class OrderKafkaListenerOfInventory {

    private static final Logger log = LoggerFactory.getLogger(OrderKafkaListenerOfInventory.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Timer eventProcessingTimer;

    OrderKafkaListenerOfInventory(OrderService orderService,
                                  KafkaTemplate<String, Object> kafkaTemplate, MeterRegistry registry) {
        this.orderService = orderService;
        this.kafkaTemplate = kafkaTemplate;
        this.eventProcessingTimer = Timer.builder("event_processing_seconds")
                .description("Time to process one Kafka event")
                .tag("service", "order-service")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }


    @KafkaListener(
            topics = "inventory-topic",
            groupId = "order-listener",
            containerFactory = "kafkaListenerContainerFactory"

    )
    void listenInventory(Map<String, Object> rawEvent) throws JsonProcessingException {
        try {
            String eventTypeName = (String) rawEvent.get("type");
            EventType type = EventType.valueOf(eventTypeName);

            if (type == EventType.ITEM_RESERVATION_CANCELLED) {
//                ItemsReservationCancelledEvent itemsReservationCancelledEvent = objectMapper.treeToValue(rootNode, ItemsReservationCancelledEvent.class);
                ItemsReservationCancelledEvent itemsReservationCancelledEvent = objectMapper.convertValue(rawEvent, ItemsReservationCancelledEvent.class);
                log.debug("обработка события ITEM_RESERVATION_CANCELLED ");
                eventProcessingTimer.record(() ->
                        orderService.cancelOrder(itemsReservationCancelledEvent.getOrderId())
                );
//                OrderCreatedEvent event = new OrderCreatedEvent(order.getOrderId(), order.getUserId(), orderItemEventList);
//                kafkaTemplate.send("orders-topic", event.getOrderId(), event);
//TODO увед что не хватило товара

            }

        } catch (Exception e) {
            System.err.println("Ошибка парсинга или обработки: " + e.getMessage());
        }

    }

}
