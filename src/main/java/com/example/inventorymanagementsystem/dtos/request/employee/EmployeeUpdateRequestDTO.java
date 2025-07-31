package com.example.inventorymanagementsystem.dtos.request.employee;

public record EmployeeUpdateRequestDTO(
    String name,
    String email,
    String department,
    Long employeeId
) {
} 