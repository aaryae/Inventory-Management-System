package com.example.inventorymanagementsystem.helper;

import com.example.inventorymanagementsystem.exception.ResourceAssignmentException;
import com.example.inventorymanagementsystem.model.Assignment;
import com.example.inventorymanagementsystem.model.Employee;
import com.example.inventorymanagementsystem.model.Resource;
import com.example.inventorymanagementsystem.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AssignmentValidationHelper {

    private final AssignmentRepository assignmentRepository;

    @Autowired
    public AssignmentValidationHelper(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    /**
     * Validates if a resource is available for assignment
     */
    public void validateResourceAvailability(Resource resource) {
        // Check if resource status is Available
        if (!"Available".equalsIgnoreCase(resource.getResourceStatus().getResourceStatusName())) {
            throw new ResourceAssignmentException(
                String.format("Resource %s is not available for assignment. Current status: %s", 
                    resource.getResourceCode(), 
                    resource.getResourceStatus().getResourceStatusName())
            );
        }

        // Check if resource is already assigned
        List<Assignment> activeAssignments = assignmentRepository.findByResourceAndIsActiveTrue(resource);
        if (!activeAssignments.isEmpty()) {
            Assignment activeAssignment = activeAssignments.get(0);
            throw new ResourceAssignmentException(
                String.format("Resource %s is already assigned to employee %s (ID: %d) since %s", 
                    resource.getResourceCode(),
                    activeAssignment.getEmployee().getName(),
                    activeAssignment.getEmployee().getEmployeeId(),
                    activeAssignment.getAssignedDate())
            );
        }
    }

    /**
     * Validates employee assignment limits
     */
    public void validateEmployeeAssignmentLimits(Employee employee, int maxAssignments) {
        List<Assignment> activeAssignments = assignmentRepository.findByEmployee_EmployeeIdAndIsActiveTrue(employee.getEmployeeId());
        
        if (activeAssignments.size() >= maxAssignments) {
            Map<String, Object> conflictData = Map.of(
                "employeeId", employee.getEmployeeId(),
                "employeeName", employee.getName(),
                "currentAssignments", activeAssignments.size(),
                "maxAllowed", maxAssignments,
                "activeAssignments", activeAssignments.stream()
                    .map(assignment -> Map.of(
                        "resourceCode", assignment.getResource().getResourceCode(),
                        "assignedDate", assignment.getAssignedDate()
                    ))
                    .collect(Collectors.toList())
            );
            
            throw new ResourceAssignmentException(
                String.format("Employee %s has reached maximum assignment limit (%d). Current assignments: %d", 
                    employee.getName(), maxAssignments, activeAssignments.size()),
                conflictData
            );
        }
    }

    /**
     * Validates assignment date conflicts
     */
    public void validateAssignmentDateConflicts(Employee employee, Resource resource, LocalDateTime expectedReturnDate) {
        // Check if employee has overlapping assignments
        List<Assignment> employeeActiveAssignments = assignmentRepository.findByEmployee_EmployeeIdAndIsActiveTrue(employee.getEmployeeId());
        
        for (Assignment existingAssignment : employeeActiveAssignments) {
            if (existingAssignment.getExpectedReturnDate() != null && expectedReturnDate != null) {
                // Check for date overlap
                if (existingAssignment.getExpectedReturnDate().isAfter(LocalDateTime.now()) && 
                    expectedReturnDate.isAfter(LocalDateTime.now())) {
                    
                    Map<String, Object> conflictData = Map.of(
                        "employeeId", employee.getEmployeeId(),
                        "employeeName", employee.getName(),
                        "conflictingAssignment", Map.of(
                            "resourceCode", existingAssignment.getResource().getResourceCode(),
                            "expectedReturnDate", existingAssignment.getExpectedReturnDate()
                        ),
                        "newAssignment", Map.of(
                            "resourceCode", resource.getResourceCode(),
                            "expectedReturnDate", expectedReturnDate
                        )
                    );
                    
                    throw new ResourceAssignmentException(
                        String.format("Employee %s has overlapping assignment periods. Conflicting resource: %s", 
                            employee.getName(), existingAssignment.getResource().getResourceCode()),
                        conflictData
                    );
                }
            }
        }
    }

    /**
     * Validates resource maintenance status
     */
    public void validateResourceMaintenanceStatus(Resource resource) {
        String status = resource.getResourceStatus().getResourceStatusName();
        
        if ("InRepair".equalsIgnoreCase(status) || "Damaged".equalsIgnoreCase(status)) {
            throw new ResourceAssignmentException(
                String.format("Resource %s cannot be assigned due to maintenance status: %s", 
                    resource.getResourceCode(), status)
            );
        }
    }

    /**
     * Validates resource warranty status
     */
    public void validateResourceWarrantyStatus(Resource resource) {
        if (resource.getWarrantyExpiry() != null && resource.getWarrantyExpiry().isBefore(java.time.LocalDate.now())) {
            throw new ResourceAssignmentException(
                String.format("Resource %s warranty has expired on %s. Assignment not recommended.", 
                    resource.getResourceCode(), resource.getWarrantyExpiry())
            );
        }
    }

    /**
     * Validates employee department restrictions
     */
    public void validateDepartmentRestrictions(Employee employee, Resource resource) {
        // Example: IT department can only assign IT equipment
        if ("IT".equalsIgnoreCase(employee.getDepartment())) {
            String resourceClass = resource.getResourceClass().getResourceClassName();
            if (!"Computer Equipment".equalsIgnoreCase(resourceClass) && 
                !"IT Equipment".equalsIgnoreCase(resourceClass)) {
                throw new ResourceAssignmentException(
                    String.format("IT department employee %s cannot be assigned %s equipment (Resource: %s)", 
                        employee.getName(), resourceClass, resource.getResourceCode())
                );
            }
        }
    }

    /**
     * Validates resource assignment history for conflicts
     */
    public void validateAssignmentHistoryConflicts(Employee employee, Resource resource) {
        // Check if this employee has had issues with this resource before
        List<Assignment> previousAssignments = assignmentRepository.findByEmployee_EmployeeId(employee.getEmployeeId());
        
        for (Assignment previousAssignment : previousAssignments) {
            if (previousAssignment.getResource().getResourceId().equals(resource.getResourceId())) {
                // Check if there were any issues in previous assignments
                if (previousAssignment.getReturnNotes() != null && 
                    (previousAssignment.getReturnNotes().toLowerCase().contains("damaged") ||
                     previousAssignment.getReturnNotes().toLowerCase().contains("lost") ||
                     previousAssignment.getReturnNotes().toLowerCase().contains("broken"))) {
                    
                    Map<String, Object> conflictData = Map.of(
                        "employeeId", employee.getEmployeeId(),
                        "employeeName", employee.getName(),
                        "resourceCode", resource.getResourceCode(),
                        "previousIssue", previousAssignment.getReturnNotes(),
                        "previousAssignmentDate", previousAssignment.getAssignedDate()
                    );
                    
                    throw new ResourceAssignmentException(
                        String.format("Employee %s has previous issues with resource %s. Previous note: %s", 
                            employee.getName(), resource.getResourceCode(), previousAssignment.getReturnNotes()),
                        conflictData
                    );
                }
            }
        }
    }

    /**
     * Validates resource availability in specific time slots
     */
    public void validateTimeSlotAvailability(Resource resource, LocalDateTime assignmentDate, LocalDateTime expectedReturnDate) {
        // Check if resource is available during the requested time period
        List<Assignment> resourceAssignments = assignmentRepository.findByResource_ResourceId(resource.getResourceId());
        
        for (Assignment existingAssignment : resourceAssignments) {
            if (existingAssignment.getIsActive() && 
                existingAssignment.getExpectedReturnDate() != null) {
                
                // Check for time overlap
                if (assignmentDate.isBefore(existingAssignment.getExpectedReturnDate()) &&
                    expectedReturnDate.isAfter(existingAssignment.getAssignedDate())) {
                    
                    Map<String, Object> conflictData = Map.of(
                        "resourceCode", resource.getResourceCode(),
                        "requestedPeriod", Map.of(
                            "start", assignmentDate,
                            "end", expectedReturnDate
                        ),
                        "conflictingAssignment", Map.of(
                            "employeeName", existingAssignment.getEmployee().getName(),
                            "start", existingAssignment.getAssignedDate(),
                            "end", existingAssignment.getExpectedReturnDate()
                        )
                    );
                    
                    throw new ResourceAssignmentException(
                        String.format("Resource %s is not available during the requested time period. " +
                            "Conflicting assignment to employee %s", 
                            resource.getResourceCode(), existingAssignment.getEmployee().getName()),
                        conflictData
                    );
                }
            }
        }
    }

    /**
     * Validates resource assignment based on resource type restrictions
     */
    public void validateResourceTypeRestrictions(Employee employee, Resource resource) {
        String resourceType = resource.getType().getResourceTypeName();
        String employeeDepartment = employee.getDepartment();
        
        // Example restrictions
        if ("Laptop".equalsIgnoreCase(resourceType) && "Janitorial".equalsIgnoreCase(employeeDepartment)) {
            throw new ResourceAssignmentException(
                String.format("Janitorial department employee %s cannot be assigned laptops (Resource: %s)", 
                    employee.getName(), resource.getResourceCode())
            );
        }
        
        if ("Server".equalsIgnoreCase(resourceType) && !"IT".equalsIgnoreCase(employeeDepartment)) {
            throw new ResourceAssignmentException(
                String.format("Only IT department employees can be assigned servers. Employee %s is in %s department", 
                    employee.getName(), employeeDepartment)
            );
        }
    }

    /**
     * Comprehensive validation for resource assignment
     */
    public void validateAssignment(Employee employee, Resource resource, LocalDateTime expectedReturnDate) {
        // Basic availability check
        validateResourceAvailability(resource);
        
        // Maintenance status check
        validateResourceMaintenanceStatus(resource);
        
        // Warranty status check
        validateResourceWarrantyStatus(resource);
        
        // Employee assignment limits
        validateEmployeeAssignmentLimits(employee, 5); // Max 5 assignments per employee
        
        // Date conflict validation
        validateAssignmentDateConflicts(employee, resource, expectedReturnDate);
        
        // Department restrictions
        validateDepartmentRestrictions(employee, resource);
        
        // Resource type restrictions
        validateResourceTypeRestrictions(employee, resource);
        
        // Assignment history conflicts
        validateAssignmentHistoryConflicts(employee, resource);
        
        // Time slot availability
        validateTimeSlotAvailability(resource, LocalDateTime.now(), expectedReturnDate);
    }
} 