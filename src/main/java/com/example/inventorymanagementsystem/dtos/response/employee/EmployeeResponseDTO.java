package com.example.inventorymanagementsystem.dtos.response.employee;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EmployeeResponseDTO(
        Long employeeId,
        String name,
        String email,
        String department,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
}
