package com.example.inventorymanagementsystem.exception;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResourceNotFoundExceptionHandler extends RuntimeException{

    private final String resourceName;
    private final String fieldName;
    private final Serializable fieldValue;

    public ResourceNotFoundExceptionHandler(String resourceName, String fieldName, Serializable fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
