package com.example.inventorymanagementsystem.controller.assignment;

import com.example.inventorymanagementsystem.dtos.request.assignments.AssignmentRequestDTO;
import com.example.inventorymanagementsystem.dtos.request.assignments.AssignmentUpdateRequestDTO;
import com.example.inventorymanagementsystem.dtos.response.assignments.AssignmentResponseDTO;
import com.example.inventorymanagementsystem.dtos.request.assignments.ReturnRequestDTO;
import com.example.inventorymanagementsystem.dtos.response.ApiResponse;
import com.example.inventorymanagementsystem.helper.MessageConstant;
import com.example.inventorymanagementsystem.service.AssignmentService;
import com.example.inventorymanagementsystem.service.impl.AssignmentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@Tag(name = "Assignment APIs", description = "Manage resource assignments to employees")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentServiceImpl assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/assign")
    @Operation(summary = "Assign resource to employee")
    public ResponseEntity<ApiResponse> assignResourceToEmployee(@RequestBody AssignmentRequestDTO requestDTO) {
        AssignmentResponseDTO responseDTO = assignmentService.assignResourceToEmployee(requestDTO);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_ASSIGNED, true, responseDTO));
    }

    @PostMapping("/return")
    @Operation(summary = "Return resource from employee")
    public ResponseEntity<ApiResponse> returnResourceFromEmployee(@RequestBody ReturnRequestDTO requestDTO) {
        AssignmentResponseDTO responseDTO = assignmentService.returnResourceFromEmployee(requestDTO);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_RETURNED, true, responseDTO));
    }

    @GetMapping("/{assignmentId}")
    @Operation(summary = "Get assignment by ID")
    public ResponseEntity<ApiResponse> getAssignmentById(@PathVariable Long assignmentId) {
        AssignmentResponseDTO responseDTO = assignmentService.getAssignmentById(assignmentId);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, responseDTO));
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active assignments")
    public ResponseEntity<ApiResponse> getAllActiveAssignments() {
        List<AssignmentResponseDTO> responseDTOs = assignmentService.getAllActiveAssignments();
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, responseDTOs));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get all assignments for an employee")
    public ResponseEntity<ApiResponse> getAssignmentsByEmployeeId(@PathVariable Long employeeId) {
        List<AssignmentResponseDTO> responseDTOs = assignmentService.getAssignmentsByEmployeeId(employeeId);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, responseDTOs));
    }

    @GetMapping("/employee/{employeeId}/active")
    @Operation(summary = "Get active assignments for an employee")
    public ResponseEntity<ApiResponse> getActiveAssignmentsByEmployeeId(@PathVariable Long employeeId) {
        List<AssignmentResponseDTO> responseDTOs = assignmentService.getActiveAssignmentsByEmployeeId(employeeId);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, responseDTOs));
    }

    @GetMapping("/resource/{resourceId}")
    @Operation(summary = "Get all assignments for a resource")
    public ResponseEntity<ApiResponse> getAssignmentsByResourceId(@PathVariable Long resourceId) {
        List<AssignmentResponseDTO> responseDTOs = assignmentService.getAssignmentsByResourceId(resourceId);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, responseDTOs));
    }

    @GetMapping("/resource/{resourceId}/active")
    @Operation(summary = "Get active assignments for a resource")
    public ResponseEntity<ApiResponse> getActiveAssignmentsByResourceId(@PathVariable Long resourceId) {
        List<AssignmentResponseDTO> responseDTOs = assignmentService.getActiveAssignmentsByResourceId(resourceId);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, responseDTOs));
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue assignments")
    public ResponseEntity<ApiResponse> getOverdueAssignments() {
        List<AssignmentResponseDTO> responseDTOs = assignmentService.getOverdueAssignments();
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, responseDTOs));
    }

    @GetMapping("/due-soon/{days}")
    @Operation(summary = "Get assignments due within specified days")
    public ResponseEntity<ApiResponse> getAssignmentsDueSoon(@PathVariable int days) {
        List<AssignmentResponseDTO> responseDTOs = assignmentService.getAssignmentsDueSoon(days);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, responseDTOs));
    }

    @PutMapping("/{assignmentId}")
    @Operation(summary = "Update assignment details")
    public ResponseEntity<ApiResponse> updateAssignment(@PathVariable Long assignmentId, @RequestBody AssignmentUpdateRequestDTO updateDTO) {
        AssignmentResponseDTO responseDTO = assignmentService.updateAssignment(assignmentId, updateDTO);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_UPDATED, true, responseDTO));
    }
} 