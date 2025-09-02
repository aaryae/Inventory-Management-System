package com.example.inventorymanagementsystem.dtos;

public record EmployeeUpdateDTO (
    String name,

    String email,

    String department,

    Long employeeId
){
}
