package com.example.inventorymanagementsystem.dtos.request.employee;

public record EmployeeRequestDTO(
        String name,
        String email,
        String department
) {
}