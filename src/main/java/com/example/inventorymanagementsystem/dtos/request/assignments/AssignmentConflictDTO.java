package com.example.inventorymanagementsystem.dtos.request.assignments;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Map;

public record AssignmentConflictDTO(
    String conflictType,
    String message,
    String resourceCode,
    String employeeName,
    Long employeeId,
    String resourceStatus,
    String department,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime conflictDate,
    Map<String, Object> conflictDetails,
    String suggestedResolution
) {} 