package com.example.common.events;


import com.example.common.enums.EventType;

import java.util.List;

public class ItemsReservationCancelledEvent extends Event {

    private String orderId;
    private List<OrderItemEvent> items;

    public ItemsReservationCancelledEvent(String orderId, List<OrderItemEvent> items) {
        this.orderId = orderId;
        this.items = items;
        type = EventType.ITEM_RESERVATION_CANCELLED;
//        type = "ITEM_RESERVATION_CANCELLED";
    }

    public ItemsReservationCancelledEvent() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<OrderItemEvent> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEvent> items) {
        this.items = items;
    }
}
