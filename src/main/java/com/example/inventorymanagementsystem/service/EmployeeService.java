package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.dtos.request.employee.EmployeeUpdateRequestDTO;
import com.example.inventorymanagementsystem.dtos.request.employee.EmployeeRequestDTO;
import com.example.inventorymanagementsystem.dtos.response.employee.EmployeeResponseDTO;
import java.util.List;

public interface EmployeeService {
    //CRUD Operations
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO employeeRequestDTO);

    List<EmployeeResponseDTO> createEmployees(List<EmployeeRequestDTO> employeeRequestDTOList);

    EmployeeResponseDTO getEmployeeById(Long employeeId);

    EmployeeResponseDTO getEmployeeByEmail(String email);

    List<EmployeeResponseDTO> getEmployeesByDepartment(String department);

    List<EmployeeResponseDTO> getAllEmployees();

    EmployeeResponseDTO updateEmployee(Long employeeId, EmployeeUpdateRequestDTO updateDTO);

    void deleteEmployee(Long employeeId);

}
