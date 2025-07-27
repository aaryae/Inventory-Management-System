package com.example.inventorymanagementsystem.helper;

import com.example.inventorymanagementsystem.dtos.response.employee.EmployeeResponseDTO;
import com.example.inventorymanagementsystem.model.Employee;

public final class EmployeeMapper {

    private EmployeeMapper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static EmployeeResponseDTO convertToDto(Employee employee) {
        if (employee == null) return null;

        return new EmployeeResponseDTO(
                employee.getEmployeeId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
    //Enum for Assignment Status
    public enum AssignmentStatus {
        PENDING_CONFIRMATION,
        CONFIRMED,
        ACTIVE,
        RETURNED,
        OVERDUE,
        LOST,
        DAMAGED
    }
}