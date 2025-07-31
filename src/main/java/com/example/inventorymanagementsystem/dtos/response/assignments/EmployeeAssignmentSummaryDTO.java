package com.example.inventorymanagementsystem.dtos.response.assignments;

import java.time.LocalDateTime;
import java.util.List;

public record EmployeeAssignmentSummaryDTO(
    Long employeeId,
    String employeeName,
    String employeeEmail,
    String employeeDepartment,
    Integer totalAssignments,
    Integer activeAssignments,
    Integer completedAssignments,
    Integer overdueAssignments,
    LocalDateTime firstAssignmentDate,
    LocalDateTime lastAssignmentDate,
    List<AssignmentHistoryDTO> assignmentHistory
) {} 