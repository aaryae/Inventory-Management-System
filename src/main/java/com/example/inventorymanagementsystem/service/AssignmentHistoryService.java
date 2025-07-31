package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.dtos.response.assignments.AssignmentHistoryDTO;
import com.example.inventorymanagementsystem.dtos.response.assignments.EmployeeAssignmentSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface AssignmentHistoryService {
    
    // Get complete assignment history for an employee
    List<AssignmentHistoryDTO> getAssignmentHistoryByEmployeeId(Long employeeId);
    
    // Get assignment history for an employee within date range
    List<AssignmentHistoryDTO> getAssignmentHistoryByEmployeeIdAndDateRange(Long employeeId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Get assignment history by employee email
    List<AssignmentHistoryDTO> getAssignmentHistoryByEmployeeEmail(String email);
    
    // Get active assignments for an employee
    List<AssignmentHistoryDTO> getActiveAssignmentsByEmployeeId(Long employeeId);
    
    // Get completed assignments for an employee
    List<AssignmentHistoryDTO> getCompletedAssignmentsByEmployeeId(Long employeeId);
    
    // Get overdue assignments for an employee
    List<AssignmentHistoryDTO> getOverdueAssignmentsByEmployeeId(Long employeeId);
    
    // Get employee assignment summary with statistics
    EmployeeAssignmentSummaryDTO getEmployeeAssignmentSummary(Long employeeId);
    
    // Get all employees with their assignment summaries
    List<EmployeeAssignmentSummaryDTO> getAllEmployeeAssignmentSummaries();
    
    // Get employees with most assignments (top N)
    List<EmployeeAssignmentSummaryDTO> getTopEmployeesByAssignmentCount(int limit);
    
    // Get employees with overdue assignments
    List<EmployeeAssignmentSummaryDTO> getEmployeesWithOverdueAssignments();
    
    // Get assignment history by department
    List<AssignmentHistoryDTO> getAssignmentHistoryByDepartment(String department);
    
    // Get assignment history by assigned by user
    List<AssignmentHistoryDTO> getAssignmentHistoryByAssignedBy(String assignedBy);
    
    // Get assignment history for a specific resource
    List<AssignmentHistoryDTO> getAssignmentHistoryByResourceId(Long resourceId);
    
    // Get assignment history for a specific resource code
    List<AssignmentHistoryDTO> getAssignmentHistoryByResourceCode(String resourceCode);
    
    // Get assignment statistics for date range
    List<AssignmentHistoryDTO> getAssignmentHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate);
} 