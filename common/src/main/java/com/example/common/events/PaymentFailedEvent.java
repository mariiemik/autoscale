package com.example.common.events;


import com.example.common.enums.EventType;

public class PaymentFailedEvent extends Event {

    private String orderId;
    private int price;

    public PaymentFailedEvent(String orderId, int price) {
        this.orderId = orderId;
        this.price = price;
//        type = "PAYMENT_FAILED";
        type = EventType.PAYMENT_FAILED;
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

    public PaymentFailedEvent() {
    }
}
