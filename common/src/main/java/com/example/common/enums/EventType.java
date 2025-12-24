package com.example.common.enums;

public enum EventType {
    ORDER_CREATED("ORDER_CREATED"),
    ORDER_CONFIRMED("ORDER_CONFIRMED"),
    ORDER_CANCELLED("ORDER_CANCELLED"),
    PAYMENT_CONFIRMED("PAYMENT_CONFIRMED"),
    PAYMENT_FAILED("PAYMENT_FAILED"),
    ITEM_RESERVED("ITEM_RESERVED"),
    ITEM_RESERVATION_CANCELLED("ITEM_RESERVATION_CANCELLED"),
    NOTIFICATION_REQUESTED("NOTIFICATION_REQUESTED");

    private final String stringType;

    private EventType(String stringType) {
        this.stringType = stringType;
    }

    public String getStringType() {
        return stringType;
    }
}
