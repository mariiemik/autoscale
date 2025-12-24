package com.example.order_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orders_items")
public class OrderItemModel {

    @EmbeddedId
    private OrderIdAndInventoryItemIdPK idAndInventoryItemIdPK;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @ManyToOne
    @MapsId("orderId") // связывает поле orderId в EmbeddedId
    @JoinColumn(name = "order_id")
    private OrderModel order;

    @Column(name = "name")
    private String name;

    public OrderItemModel(OrderIdAndInventoryItemIdPK idAndInventoryItemIdPK, int quantity, String name) {
        this.idAndInventoryItemIdPK = idAndInventoryItemIdPK;
        this.quantity = quantity;
        this.name = name;
    }

    public OrderItemModel() {
    }

    public OrderIdAndInventoryItemIdPK getIdAndInventoryItemIdPK() {
        return idAndInventoryItemIdPK;
    }

    public void setIdAndInventoryItemIdPK(OrderIdAndInventoryItemIdPK idAndInventoryItemIdPK) {
        this.idAndInventoryItemIdPK = idAndInventoryItemIdPK;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderModel getOrder() {
        return order;
    }

    public void setOrder(OrderModel order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
