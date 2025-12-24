package com.example.inventory_service.exception;

public class InvalidItemQuantityException extends RuntimeException {
    public InvalidItemQuantityException(String message) {
        super(message);
    }
}
