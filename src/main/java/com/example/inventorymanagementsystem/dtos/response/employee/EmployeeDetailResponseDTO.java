package com.example.inventorymanagementsystem.dtos.response.employee;

import java.time.LocalDateTime;
import java.util.List;
import com.example.inventorymanagementsystem.dtos.response.assignments.EmployeeAssignmentSummaryDTO;

public record EmployeeDetailResponseDTO(
    Long id,
    String name,
    String email,
    String department,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<EmployeeAssignmentSummaryDTO> activeAssignments,
    Integer totalAssignedResources,
    String status
) {
} 