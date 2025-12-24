package com.example.common.events;


import com.example.common.enums.EventType;

import java.util.List;

public class OrderCreatedEvent extends Event {

    private String orderId;
    private String userId;
    private List<OrderItemEvent> items;

    public OrderCreatedEvent(String orderId, String userId, List<OrderItemEvent> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
//        type = "ORDER_CREATED";
        type = EventType.ORDER_CREATED;
    }

    public OrderCreatedEvent() {
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<OrderItemEvent> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEvent> items) {
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }
}
