package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.dtos.request.assignments.AssignmentRequestDTO;
import com.example.inventorymanagementsystem.dtos.request.assignments.AssignmentUpdateRequestDTO;
import com.example.inventorymanagementsystem.dtos.response.assignments.AssignmentResponseDTO;
import com.example.inventorymanagementsystem.dtos.request.assignments.ReturnRequestDTO;
import com.example.inventorymanagementsystem.exception.*;
import com.example.inventorymanagementsystem.helper.MessageConstant;
import com.example.inventorymanagementsystem.model.Assignment;
import com.example.inventorymanagementsystem.model.Employee;
import com.example.inventorymanagementsystem.model.Resource;
import com.example.inventorymanagementsystem.repository.AssignmentRepository;
import com.example.inventorymanagementsystem.repository.EmployeeRepository;
import com.example.inventorymanagementsystem.repository.ResourceRepository;
import com.example.inventorymanagementsystem.service.AssignmentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ResourceRepository resourceRepository;

    @Autowired
    public AssignmentServiceImpl(AssignmentRepository assignmentRepository, 
                              EmployeeRepository employeeRepository, 
                              ResourceRepository resourceRepository) {
        this.assignmentRepository = assignmentRepository;
        this.employeeRepository = employeeRepository;
        this.resourceRepository = resourceRepository;
    }

    @Override
    @Transactional
    public AssignmentResponseDTO assignResourceToEmployee(AssignmentRequestDTO requestDTO) {
        // Validate employee exists
        Employee employee = employeeRepository.findById(requestDTO.employeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(MessageConstant.EMPLOYEE, "id", requestDTO.employeeId()));

        // Validate resource exists
        Resource resource = resourceRepository.findById(requestDTO.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.RESOURCE, "id", requestDTO.resourceId()));

        // Check if resource is already assigned
        List<Assignment> activeAssignments = assignmentRepository.findByResourceAndIsActiveTrue(resource);
        if (!activeAssignments.isEmpty()) {
            throw new BusinessLogicException("Resource is already assigned to another employee", false, null, org.springframework.http.HttpStatus.CONFLICT);
        }

        // Check if employee has too many active assignments (optional limit)
        List<Assignment> employeeActiveAssignments = assignmentRepository.findByEmployee_EmployeeIdAndIsActiveTrue(requestDTO.employeeId());
        if (employeeActiveAssignments.size() >= 5) { // Limit of 5 active assignments per employee
            throw new BusinessLogicException("Employee has reached maximum number of active assignments", false, null, org.springframework.http.HttpStatus.CONFLICT);
        }

        // Create new assignment
        Assignment assignment = new Assignment();
        assignment.setEmployee(employee);
        assignment.setResource(resource);
        assignment.setExpectedReturnDate(requestDTO.expectedReturnDate());
        assignment.setIsActive(true);
        assignment.setAssignedBy(getCurrentUsername());

        Assignment savedAssignment = assignmentRepository.save(assignment);
        return convertToResponseDTO(savedAssignment);
    }

    @Override
    @Transactional
    public AssignmentResponseDTO returnResourceFromEmployee(ReturnRequestDTO requestDTO) {
        // Find the assignment
        Assignment assignment = assignmentRepository.findById(requestDTO.assignmentId())
                .orElseThrow(() -> new DataNotFoundException("Assignment not found with id: " + requestDTO.assignmentId()));

        // Check if assignment is active
        if (!assignment.getIsActive()) {
            throw new BusinessLogicException("Assignment is already returned", false, null, org.springframework.http.HttpStatus.CONFLICT);
        }

        // Update assignment
        assignment.setIsActive(false);
        assignment.setReturnDate(LocalDateTime.now());
        assignment.setReturnNotes(requestDTO.returnNotes());

        Assignment updatedAssignment = assignmentRepository.save(assignment);
        return convertToResponseDTO(updatedAssignment);
    }

    @Override
    public AssignmentResponseDTO getAssignmentById(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new DataNotFoundException("Assignment not found with id: " + assignmentId));
        return convertToResponseDTO(assignment);
    }

    @Override
    public List<AssignmentResponseDTO> getAllActiveAssignments() {
        List<Assignment> assignments = assignmentRepository.findByIsActiveTrue();
        return assignments.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentResponseDTO> getAssignmentsByEmployeeId(Long employeeId) {
        List<Assignment> assignments = assignmentRepository.findByEmployee_EmployeeId(employeeId);
        return assignments.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentResponseDTO> getActiveAssignmentsByEmployeeId(Long employeeId) {
        List<Assignment> assignments = assignmentRepository.findByEmployee_EmployeeIdAndIsActiveTrue(employeeId);
        return assignments.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentResponseDTO> getAssignmentsByResourceId(Long resourceId) {
        List<Assignment> assignments = assignmentRepository.findByResource_ResourceId(resourceId);
        return assignments.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentResponseDTO> getActiveAssignmentsByResourceId(Long resourceId) {
        List<Assignment> assignments = assignmentRepository.findByResource_ResourceIdAndIsActiveTrue(resourceId);
        return assignments.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentResponseDTO> getOverdueAssignments() {
        LocalDateTime now = LocalDateTime.now();
        List<Assignment> activeAssignments = assignmentRepository.findByIsActiveTrue();
        
        return activeAssignments.stream()
                .filter(assignment -> assignment.getExpectedReturnDate() != null && 
                        assignment.getExpectedReturnDate().isBefore(now))
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentResponseDTO> getAssignmentsDueSoon(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusDays(days);
        List<Assignment> activeAssignments = assignmentRepository.findByIsActiveTrue();
        
        return activeAssignments.stream()
                .filter(assignment -> assignment.getExpectedReturnDate() != null && 
                        assignment.getExpectedReturnDate().isAfter(now) && 
                        assignment.getExpectedReturnDate().isBefore(dueDate))
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AssignmentResponseDTO updateAssignment(Long assignmentId, AssignmentUpdateRequestDTO updateDTO) {
        // Find the assignment
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new DataNotFoundException("Assignment not found with id: " + assignmentId));

        // Update fields if provided
        if (updateDTO.employeeId() != null) {
            Employee employee = employeeRepository.findById(updateDTO.employeeId())
                    .orElseThrow(() -> new EmployeeNotFoundException(MessageConstant.EMPLOYEE, "id", updateDTO.employeeId()));
            assignment.setEmployee(employee);
        }

        if (updateDTO.resourceId() != null) {
            Resource resource = resourceRepository.findById(updateDTO.resourceId())
                    .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.RESOURCE, "id", updateDTO.resourceId()));
            assignment.setResource(resource);
        }

        if (updateDTO.assignedDate() != null) {
            assignment.setAssignedDate(updateDTO.assignedDate());
        }

        if (updateDTO.expectedReturnDate() != null) {
            assignment.setExpectedReturnDate(updateDTO.expectedReturnDate());
        }

        if (updateDTO.notes() != null) {
            assignment.setReturnNotes(updateDTO.notes());
        }

        if (updateDTO.status() != null) {
            assignment.setIsActive("ACTIVE".equalsIgnoreCase(updateDTO.status()));
        }

        // Save the updated assignment
        Assignment updatedAssignment = assignmentRepository.save(assignment);
        return convertToResponseDTO(updatedAssignment);
    }

    private AssignmentResponseDTO convertToResponseDTO(Assignment assignment) {
        return new AssignmentResponseDTO(
                assignment.getAssignmentId(),
                assignment.getEmployee().getEmployeeId(),
                assignment.getEmployee().getName(),
                assignment.getEmployee().getEmail(),
                assignment.getResource().getResourceId(),
                assignment.getResource().getResourceCode(),
                assignment.getResource().getBrand(),
                assignment.getResource().getModel(),
                assignment.getAssignedDate(),
                assignment.getExpectedReturnDate(),
                assignment.getReturnDate(),
                assignment.getIsActive(),
                assignment.getAssignedBy(),
                assignment.getReturnNotes(),
                assignment.getCreatedAt(),
                assignment.getUpdatedAt()
        );
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
} 