package com.example.common.events;


import com.example.common.enums.EventType;

import java.util.List;

public class ItemsReservedEvent extends Event {

    private String orderId;
    private List<OrderItemEvent> items;

    public ItemsReservedEvent(String orderId, List<OrderItemEvent> items) {
        this.orderId = orderId;
        this.items = items;
//        type = "ITEM_RESERVED";
        type = EventType.ITEM_RESERVED;
    }

    public ItemsReservedEvent() {
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
