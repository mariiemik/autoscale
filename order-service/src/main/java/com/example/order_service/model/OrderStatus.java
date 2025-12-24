package com.example.order_service.model;

public enum OrderStatus {
    CREATED("CREATED"),
    CONFIRMED("CONFIRMED"),
    CANCELLED("CANCELLED");

    private final String status;

    private OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
