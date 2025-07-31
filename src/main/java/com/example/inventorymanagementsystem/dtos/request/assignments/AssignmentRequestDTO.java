package com.example.inventorymanagementsystem.dtos.request.assignments;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import java.time.LocalDateTime;

public record AssignmentRequestDTO(
    @NotNull(message = "Employee ID is required")
    Long employeeId,
    
    @NotNull(message = "Resource ID is required")
    Long resourceId,
    
    @Future(message = "Expected return date must be in the future")
    LocalDateTime expectedReturnDate,
    
    String notes
) {}
