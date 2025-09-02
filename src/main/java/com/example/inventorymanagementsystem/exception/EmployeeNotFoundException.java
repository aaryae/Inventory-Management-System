package com.example.inventorymanagementsystem.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class EmployeeNotFoundException extends RuntimeException{

    private final String employeeName;
    private final String fieldName;
    private final Serializable fieldValue;

    public EmployeeNotFoundException(String employeeName, String fieldName, Serializable fieldValue) {
        super(String.format("%s not found with %s : '%s'", employeeName, fieldName, fieldValue));
        this.employeeName = employeeName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
