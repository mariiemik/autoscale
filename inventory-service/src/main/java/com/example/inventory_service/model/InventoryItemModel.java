package com.example.inventory_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;


@Entity
@Table(name = "inventory_items")
public class InventoryItemModel {
    @Id
    @Column(name = "inventory_item_id")
    private String inventoryItemId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "reserved_quantity")
    private int reservedQuantity;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private int price;

    public InventoryItemModel() {
        inventoryItemId = UUID.randomUUID().toString();
    }

    public InventoryItemModel(String name, int reservedQuantity, int quantity, int price) {
        inventoryItemId = UUID.randomUUID().toString();
        this.name = name;
        this.reservedQuantity = reservedQuantity;
        this.quantity = quantity;
        this.price = price;
    }

    public String getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(String inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(int reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
