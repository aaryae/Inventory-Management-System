package com.example.inventorymanagementsystem.exception;

public class BatchLimitException extends RuntimeException {
    public BatchLimitException(String message) {
        super(message);
    }
}
