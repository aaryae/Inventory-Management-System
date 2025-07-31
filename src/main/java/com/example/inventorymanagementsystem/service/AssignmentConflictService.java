package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.dtos.request.assignments.AssignmentConflictDTO;
import com.example.inventorymanagementsystem.model.Employee;
import com.example.inventorymanagementsystem.model.Resource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AssignmentConflictService {
    
    // Check for potential conflicts before assignment
    List<AssignmentConflictDTO> checkAssignmentConflicts(Long employeeId, Long resourceId, LocalDateTime expectedReturnDate);
    
    // Get alternative resources for an employee
    List<Resource> getAlternativeResources(Long employeeId, String resourceType);
    
    // Get employees who can be assigned a specific resource
    List<Employee> getAvailableEmployeesForResource(Long resourceId);
    
    // Check resource availability in time slot
    boolean isResourceAvailableInTimeSlot(Long resourceId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Get assignment conflicts for a specific resource
    List<AssignmentConflictDTO> getResourceAssignmentConflicts(Long resourceId);
    
    // Get assignment conflicts for a specific employee
    List<AssignmentConflictDTO> getEmployeeAssignmentConflicts(Long employeeId);
    
    // Suggest resolution for assignment conflict
    Map<String, Object> suggestConflictResolution(AssignmentConflictDTO conflict);
    
    // Force assignment (override conflicts) - admin only
    boolean forceAssignment(Long employeeId, Long resourceId, LocalDateTime expectedReturnDate, String overrideReason);
    
    // Get assignment statistics for conflict analysis
    Map<String, Object> getAssignmentConflictStatistics();
} 