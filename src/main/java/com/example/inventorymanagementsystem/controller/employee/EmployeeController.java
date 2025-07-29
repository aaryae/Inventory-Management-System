package com.example.inventorymanagementsystem.controller.employee;

import com.example.inventorymanagementsystem.dtos.EmployeeUpdateDTO;
import com.example.inventorymanagementsystem.dtos.request.employee.EmployeeRequestDTO;
import com.example.inventorymanagementsystem.dtos.response.ApiResponse;
import com.example.inventorymanagementsystem.dtos.response.employee.EmployeeResponseDTO;
import com.example.inventorymanagementsystem.helper.MessageConstant;
import com.example.inventorymanagementsystem.service.EmployeeService;
import com.example.inventorymanagementsystem.service.impl.EmployeeServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee APIs", description = "CRUD for Employees")

public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeServiceImpl employeeService) {
        this.employeeService = employeeService; }

    @PostMapping
    public ResponseEntity<ApiResponse> createEmployee(@RequestBody EmployeeRequestDTO employeeRequestDTO) {
        EmployeeResponseDTO employeeResponseDTO = employeeService.createEmployee(employeeRequestDTO);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_ADDED,true,employeeResponseDTO));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse> createEmployees(@RequestBody List<EmployeeRequestDTO> employeeRequestDTO) {
        List<EmployeeResponseDTO> employeeResponseDTO = employeeService.createEmployees(employeeRequestDTO);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_ADDED,true,employeeResponseDTO));
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<ApiResponse> getEmployeeById(@PathVariable("employeeId") Long employeeId){
        EmployeeResponseDTO employeeResponseDTO = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED,true,employeeResponseDTO));
    }

    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse> getEmployeeByEmail(@PathVariable("email") String email){
        EmployeeResponseDTO employeeResponseDTO = employeeService.getEmployeeByEmail(email);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED,true,employeeResponseDTO));
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse> getEmployeesByDepartment(@PathVariable("department") String department){
        List<EmployeeResponseDTO> employeeResponseDTOList = employeeService.getEmployeesByDepartment(department);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED,true,employeeResponseDTOList));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllEmployees(){
        List<EmployeeResponseDTO> employeeResponseDTOList = employeeService.getAllEmployees();
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED,true, employeeResponseDTOList));
    }
    @PutMapping("/{employeeId}")
    public ResponseEntity<ApiResponse> updateEmployees(@PathVariable("employeeId") Long employeeId, @RequestBody EmployeeUpdateDTO employeeUpdateDTO){
        EmployeeResponseDTO employeeResponseDTO = employeeService.updateEmployee(employeeId, employeeUpdateDTO);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_UPDATED,true,employeeResponseDTO));
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<ApiResponse> deleteEmployees(@PathVariable("employeeId") Long employeeId){
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_DELETED,true));
    }
}