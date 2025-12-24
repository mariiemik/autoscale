package com.example.common.events;


import com.example.common.enums.EventType;

import java.util.List;

public class OrderCancelledEvent extends Event {
    private String orderId;
    private String userId;
    private int price;
    private List<OrderItemEvent> items;

    public OrderCancelledEvent(String orderId, String userId, int price, List<OrderItemEvent> items) {
        this.orderId = orderId;
        this.price = price;
        this.items = items;
        this.userId = userId;
//        type = "ORDER_CANCELLED";
        type = EventType.ORDER_CANCELLED;
    }

    public OrderCancelledEvent() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<OrderItemEvent> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEvent> items) {
        this.items = items;
    }
}
