package com.example.inventorymanagementsystem.dtos.request.assignments;

import jakarta.validation.constraints.NotNull;

public record ReturnRequestDTO(
    @NotNull(message = "Assignment ID is required")
    Long assignmentId,
    
    String returnNotes
) {} 