package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.dtos.request.assignments.AssignmentRequestDTO;
import com.example.inventorymanagementsystem.dtos.request.assignments.AssignmentUpdateRequestDTO;
import com.example.inventorymanagementsystem.dtos.response.assignments.AssignmentResponseDTO;
import com.example.inventorymanagementsystem.dtos.request.assignments.ReturnRequestDTO;

import java.util.List;

public interface AssignmentService {
    
    // Assign resource to employee
    AssignmentResponseDTO assignResourceToEmployee(AssignmentRequestDTO requestDTO);
    
    // Return resource from employee
    AssignmentResponseDTO returnResourceFromEmployee(ReturnRequestDTO requestDTO);
    
    // Get assignment by ID
    AssignmentResponseDTO getAssignmentById(Long assignmentId);
    
    // Get all active assignments
    List<AssignmentResponseDTO> getAllActiveAssignments();
    
    // Get assignments by employee ID
    List<AssignmentResponseDTO> getAssignmentsByEmployeeId(Long employeeId);
    
    // Get active assignments by employee ID
    List<AssignmentResponseDTO> getActiveAssignmentsByEmployeeId(Long employeeId);
    
    // Get assignments by resource ID
    List<AssignmentResponseDTO> getAssignmentsByResourceId(Long resourceId);
    
    // Get active assignments by resource ID
    List<AssignmentResponseDTO> getActiveAssignmentsByResourceId(Long resourceId);
    
    // Get overdue assignments
    List<AssignmentResponseDTO> getOverdueAssignments();
    
    // Get assignments due soon (within specified days)
    List<AssignmentResponseDTO> getAssignmentsDueSoon(int days);
    
    // Update assignment
    AssignmentResponseDTO updateAssignment(Long assignmentId, AssignmentUpdateRequestDTO updateDTO);
} 