package com.example.payment_service.model;

import com.example.common.dto.PaymentStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class PaymentModel {

    @Id
    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public PaymentModel() {
    }

    public PaymentModel(String orderId, int price, PaymentStatus status) {
        this.orderId = orderId;
        this.price = price;
        this.status = status;
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

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}


