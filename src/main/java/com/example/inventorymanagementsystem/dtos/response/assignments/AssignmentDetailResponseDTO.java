package com.example.inventorymanagementsystem.dtos.response.assignments;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AssignmentDetailResponseDTO(
    Long id,
    Long employeeId,
    String employeeName,
    String employeeEmail,
    String employeeDepartment,
    Long resourceId,
    String resourceName,
    String resourceBrand,
    String resourceModel,
    String resourceSpecification,
    LocalDate assignedDate,
    LocalDate expectedReturnDate,
    LocalDate actualReturnDate,
    String status,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Boolean isOverdue,
    Integer daysOverdue
) {
} 