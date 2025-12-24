package com.example.common.events;


import com.example.common.enums.EventType;

public class PaymentConfirmedEvent extends Event {

    private String orderId;
    private int price;

    public PaymentConfirmedEvent(String orderId, int price) {
        this.orderId = orderId;
        this.price = price;
//        type = "PAYMENT_CONFIRMED";
        type = EventType.PAYMENT_CONFIRMED;
    }

    public PaymentConfirmedEvent() {
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
}
