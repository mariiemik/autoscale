package com.example.order_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderIdAndInventoryItemIdPK implements Serializable {
    @Column(name = "order_id")
    private String orderId;

    @Column(name = "inventory_item_id")
    private String inventoryItemId;

    public OrderIdAndInventoryItemIdPK() {
    }

    public OrderIdAndInventoryItemIdPK(String orderId, String inventoryItemId) {
        this.orderId = orderId;
        this.inventoryItemId = inventoryItemId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(String inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderIdAndInventoryItemIdPK that = (OrderIdAndInventoryItemIdPK) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(inventoryItemId, that.inventoryItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, inventoryItemId);
    }
}
