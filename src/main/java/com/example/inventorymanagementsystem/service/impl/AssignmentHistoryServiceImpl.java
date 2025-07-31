package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.dtos.response.assignments.AssignmentHistoryDTO;
import com.example.inventorymanagementsystem.dtos.response.assignments.EmployeeAssignmentSummaryDTO;
import com.example.inventorymanagementsystem.exception.EmployeeNotFoundException;
import com.example.inventorymanagementsystem.helper.MessageConstant;
import com.example.inventorymanagementsystem.model.Assignment;
import com.example.inventorymanagementsystem.model.Employee;
import com.example.inventorymanagementsystem.repository.AssignmentRepository;
import com.example.inventorymanagementsystem.repository.EmployeeRepository;
import com.example.inventorymanagementsystem.service.AssignmentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class AssignmentHistoryServiceImpl implements AssignmentHistoryService {

    private final AssignmentRepository assignmentRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public AssignmentHistoryServiceImpl(AssignmentRepository assignmentRepository, EmployeeRepository employeeRepository) {
        this.assignmentRepository = assignmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<AssignmentHistoryDTO> getAssignmentHistoryByEmployeeId(Long employeeId) {
        // Validate employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(MessageConstant.EMPLOYEE, "id", employeeId));

        List<Assignment> assignments = assignmentRepository.findByEmployee_EmployeeId(employeeId);
        return assignments.stream()
                .sorted(Comparator.comparing(Assignment::getAssignedDate).reversed())
                .map(this::convertToHistoryDTO)
                .toList();
    }

    @Override
    public List<AssignmentHistoryDTO> getAssignmentHistoryByEmployeeIdAndDateRange(Long employeeId, LocalDateTime startDate, LocalDateTime endDate) {
        // Validate employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(MessageConstant.EMPLOYEE, "id", employeeId));

        List<Assignment> assignments = assignmentRepository.findByEmployee_EmployeeId(employeeId);
        return assignments.stream()
                .filter(assignment -> assignment.getAssignedDate().isAfter(startDate) && assignment.getAssignedDate().isBefore(endDate))
                .sorted(Comparator.comparing(Assignment::getAssignedDate).reversed())
                .map(this::convertToHistoryDTO)
                .toList();
    }

    @Override
    public List<AssignmentHistoryDTO> getAssignmentHistoryByEmployeeEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(MessageConstant.EMPLOYEE, "email", email));

        return getAssignmentHistoryByEmployeeId(employee.getEmployeeId());
    }

    @Override
    public List<AssignmentHistoryDTO> getActiveAssignmentsByEmployeeId(Long employeeId) {
        List<Assignment> assignments = assignmentRepository.findByEmployee_EmployeeIdAndIsActiveTrue(employeeId);
        return assignments.stream()
                .sorted(Comparator.comparing(Assignment::getAssignedDate).reversed())
                .map(this::convertToHistoryDTO)
                .toList();
    }

    @Override
    public List<AssignmentHistoryDTO> getCompletedAssignmentsByEmployeeId(Long employeeId) {
        List<Assignment> assignments = assignmentRepository.findByEmployee_EmployeeId(employeeId);
        return assignments.stream()
                .filter(assignment -> !assignment.getIsActive() && assignment.getReturnDate() != null)
                .sorted(Comparator.comparing(Assignment::getReturnDate).reversed())
                .map(this::convertToHistoryDTO)
                .toList();
    }

    @Override
    public List<AssignmentHistoryDTO> getOverdueAssignmentsByEmployeeId(Long employeeId) {
        LocalDateTime now = LocalDateTime.now();
        List<Assignment> assignments = assignmentRepository.findByEmployee_EmployeeIdAndIsActiveTrue(employeeId);
        
        return assignments.stream()
                .filter(assignment -> assignment.getExpectedReturnDate() != null && 
                        assignment.getExpectedReturnDate().isBefore(now))
                .sorted(Comparator.comparing(Assignment::getExpectedReturnDate))
                .map(this::convertToHistoryDTO)
                .toList();
    }

    @Override
    public EmployeeAssignmentSummaryDTO getEmployeeAssignmentSummary(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(MessageConstant.EMPLOYEE, "id", employeeId));

        List<Assignment> allAssignments = assignmentRepository.findByEmployee_EmployeeId(employeeId);
        List<Assignment> activeAssignments = assignmentRepository.findByEmployee_EmployeeIdAndIsActiveTrue(employeeId);
        
        // Calculate statistics
        int totalAssignments = allAssignments.size();
        int activeAssignmentsCount = activeAssignments.size();
        int completedAssignments = totalAssignments - activeAssignmentsCount;
        
        // Calculate overdue assignments
        LocalDateTime now = LocalDateTime.now();
        int overdueAssignments = (int) activeAssignments.stream()
                .filter(assignment -> assignment.getExpectedReturnDate() != null && 
                        assignment.getExpectedReturnDate().isBefore(now))
                .count();

        // Get first and last assignment dates
        LocalDateTime firstAssignmentDate = allAssignments.stream()
                .map(Assignment::getAssignedDate)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime lastAssignmentDate = allAssignments.stream()
                .map(Assignment::getAssignedDate)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        // Convert to history DTOs
        List<AssignmentHistoryDTO> assignmentHistory = allAssignments.stream()
                .sorted(Comparator.comparing(Assignment::getAssignedDate).reversed())
                .map(this::convertToHistoryDTO)
                .toList();

        return new EmployeeAssignmentSummaryDTO(
                employee.getEmployeeId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                totalAssignments,
                activeAssignmentsCount,
                completedAssignments,
                overdueAssignments,
                firstAssignmentDate,
                lastAssignmentDate,
                assignmentHistory
        );
    }

    @Override
    public List<EmployeeAssignmentSummaryDTO> getAllEmployeeAssignmentSummaries() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(employee -> getEmployeeAssignmentSummary(employee.getEmployeeId()))
                .toList();
    }

    @Override
    public List<EmployeeAssignmentSummaryDTO> getTopEmployeesByAssignmentCount(int limit) {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(employee -> getEmployeeAssignmentSummary(employee.getEmployeeId()))
                .sorted(Comparator.comparing(EmployeeAssignmentSummaryDTO::totalAssignments).reversed())
                .limit(limit)
                .toList();
    }

    @Override
    public List<EmployeeAssignmentSummaryDTO> getEmployeesWithOverdueAssignments() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(employee -> getEmployeeAssignmentSummary(employee.getEmployeeId()))
                .filter(summary -> summary.overdueAssignments() > 0)
                .sorted(Comparator.comparing(EmployeeAssignmentSummaryDTO::overdueAssignments).reversed())
                .toList();
    }

    @Override
    public List<AssignmentHistoryDTO> getAssignmentHistoryByDepartment(String department) {
        List<Employee> employees = employeeRepository.findByDepartmentIgnoreCase(department);
        return employees.stream()
                .flatMap(employee -> assignmentRepository.findByEmployee_EmployeeId(employee.getEmployeeId()).stream())
                .sorted(Comparator.comparing(Assignment::getAssignedDate).reversed())
                .map(this::convertToHistoryDTO)
                .toList();
    }

    @Override
    public List<AssignmentHistoryDTO> getAssignmentHistoryByAssignedBy(String assignedBy) {
        List<Assignment> assignments = assignmentRepository.findByIsActiveTrue();
        return assignments.stream()
                .filter(assignment -> assignedBy.equals(assignment.getAssignedBy()))
                .sorted(Comparator.comparing(Assignment::getAssignedDate).reversed())
                .map(this::convertToHistoryDTO)
                .toList();
    }

    @Override
    public List<AssignmentHistoryDTO> getAssignmentHistoryByResourceId(Long resourceId) {
        List<Assignment> assignments = assignmentRepository.findByResource_ResourceId(resourceId);
        return assignments.stream()
                .sorted(Comparator.comparing(Assignment::getAssignedDate).reversed())
                .map(this::convertToHistoryDTO)
                .toList();
    }

    @Override
    public List<AssignmentHistoryDTO> getAssignmentHistoryByResourceCode(String resourceCode) {
        // This would require a custom query or filtering by resource code
        // For now, we'll get all assignments and filter by resource code
        List<Assignment> assignments = assignmentRepository.findByIsActiveTrue();
        return assignments.stream()
                .filter(assignment -> resourceCode.equals(assignment.getResource().getResourceCode()))
                .sorted(Comparator.comparing(Assignment::getAssignedDate).reversed())
                .map(this::convertToHistoryDTO)
                .toList();
    }

    @Override
    public List<AssignmentHistoryDTO> getAssignmentHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Assignment> assignments = assignmentRepository.findByIsActiveTrue();
        return assignments.stream()
                .filter(assignment -> assignment.getAssignedDate().isAfter(startDate) && 
                        assignment.getAssignedDate().isBefore(endDate))
                .sorted(Comparator.comparing(Assignment::getAssignedDate).reversed())
                .map(this::convertToHistoryDTO)
                .toList();
    }

    private AssignmentHistoryDTO convertToHistoryDTO(Assignment assignment) {
        return new AssignmentHistoryDTO(
                assignment.getAssignmentId(),
                assignment.getEmployee().getEmployeeId(),
                assignment.getEmployee().getName(),
                assignment.getEmployee().getEmail(),
                assignment.getEmployee().getDepartment(),
                assignment.getResource().getResourceId(),
                assignment.getResource().getResourceCode(),
                assignment.getResource().getBrand(),
                assignment.getResource().getModel(),
                assignment.getResource().getType().getResourceTypeName(),
                assignment.getResource().getResourceClass().getResourceClassName(),
                assignment.getResource().getResourceStatus().getResourceStatusName(),
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
} 