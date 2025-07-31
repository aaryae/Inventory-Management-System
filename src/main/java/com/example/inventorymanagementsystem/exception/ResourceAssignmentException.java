package com.example.inventorymanagementsystem.exception;

import org.springframework.http.HttpStatus;

public class ResourceAssignmentException extends BusinessLogicException {
    
    public ResourceAssignmentException(String message, boolean success, Object data, HttpStatus status) {
        super(message, success, data, status);
    }
    
    public ResourceAssignmentException(String message) {
        super(message, false, null, HttpStatus.CONFLICT);
    }
    
    public ResourceAssignmentException(String message, Object data) {
        super(message, false, data, HttpStatus.CONFLICT);
    }
} 