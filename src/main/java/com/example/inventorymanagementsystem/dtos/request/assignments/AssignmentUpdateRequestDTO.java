package com.example.inventorymanagementsystem.dtos.request.assignments;

import java.time.LocalDateTime;

public record AssignmentUpdateRequestDTO(
    Long assignmentId,
    Long employeeId,
    Long resourceId,
    LocalDateTime assignedDate,
    LocalDateTime expectedReturnDate,
    String notes,
    String status
) {
} 