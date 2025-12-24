package com.example.common.events;


import com.example.common.enums.EventType;

import java.util.List;

public class OrderConfirmedEvent extends Event {

    private String orderId;
    private String userId;
    private int price;
    private List<OrderItemEvent> items;


    public OrderConfirmedEvent(String orderId, String userId, int price, List<OrderItemEvent> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.price = price;
        this.items = items;
//        type = "ORDER_CONFIRMED";
        type = EventType.ORDER_CONFIRMED;
    }

    public OrderConfirmedEvent() {
    }

    public String getOrderId() {
        return orderId;
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
