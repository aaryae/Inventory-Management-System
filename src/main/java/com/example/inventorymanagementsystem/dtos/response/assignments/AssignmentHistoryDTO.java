package com.example.inventorymanagementsystem.dtos.response.assignments;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record AssignmentHistoryDTO(
    Long assignmentId,
    Long employeeId,
    String employeeName,
    String employeeEmail,
    String employeeDepartment,
    Long resourceId,
    String resourceCode,
    String resourceBrand,
    String resourceModel,
    String resourceType,
    String resourceClass,
    String resourceStatus,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime assignedDate,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime expectedReturnDate,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime returnDate,
    Boolean isActive,
    String assignedBy,
    String returnNotes,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime createdAt,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime updatedAt
) {} 