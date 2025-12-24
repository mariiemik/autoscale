package com.example.payment_service.listener;

import com.example.common.enums.EventType;
import com.example.common.events.ItemsReservedEvent;
import com.example.common.events.OrderItemEvent;
import com.example.payment_service.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component

public class PaymentKafkaListener {
    private static final Logger log = LoggerFactory.getLogger(PaymentKafkaListener.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PaymentService paymentService;

    public PaymentKafkaListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @KafkaListener(
            topics = "inventory-topic",
            groupId = "payment-listener",
            containerFactory = "kafkaListenerContainerFactory"
    )
    void listenItems(Map<String, Object> rawEvent) throws JsonProcessingException {
        try {
            String eventTypeName = (String) rawEvent.get("type");
            EventType type = EventType.valueOf(eventTypeName);
            switch (type) {
                case ITEM_RESERVED -> {
                    log.debug("обработка события ITEM_RESERVED ");
                    ItemsReservedEvent itemsReservedEvent = objectMapper.convertValue(rawEvent, ItemsReservedEvent.class);

                    int totalPrice = 0;
                    for (OrderItemEvent itemEvent : itemsReservedEvent.getItems()) {
                        totalPrice += itemEvent.getPrice() * itemEvent.getQuantity();
                    }
                    paymentService.payForOrder(itemsReservedEvent.getOrderId(), totalPrice);

                }


            }

        } catch (Exception e) {
            System.err.println("Ошибка парсинга или обработки: " + e.getMessage());
        }

    }

}
