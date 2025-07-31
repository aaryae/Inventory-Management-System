package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.dtos.request.employee.EmployeeUpdateRequestDTO;
import com.example.inventorymanagementsystem.dtos.request.employee.EmployeeRequestDTO;
import com.example.inventorymanagementsystem.dtos.response.employee.EmployeeResponseDTO;
import com.example.inventorymanagementsystem.exception.ConflictException;
import com.example.inventorymanagementsystem.exception.DuplicateEmployeeException;
import com.example.inventorymanagementsystem.exception.EmployeeNotFoundException;
import com.example.inventorymanagementsystem.helper.MessageConstant;
import com.example.inventorymanagementsystem.model.Employee;
import com.example.inventorymanagementsystem.repository.EmployeeRepository;
import com.example.inventorymanagementsystem.service.EmployeeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO employeeRequestDTO) {
        if (employeeRepository.existsByEmail(employeeRequestDTO.email())){
            throw new DuplicateEmployeeException("Employee already exists with this email"+ employeeRequestDTO.email());
        }

        Employee employee = new Employee();
        employee.setName(employeeRequestDTO.name());
        employee.setEmail(employeeRequestDTO.email());
        employee.setDepartment(employeeRequestDTO.department());

        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDto(savedEmployee);
    }

    @Override
    @Transactional
    public List<EmployeeResponseDTO> createEmployees(List<EmployeeRequestDTO> employeeRequestDTOList) {
        //Extracts all emails from the request to check for duplicates in one callout.
        Set<String> emailsInRequest = employeeRequestDTOList.stream()
                .map(EmployeeRequestDTO::email)
                .collect(Collectors.toSet());
        //Check for duplicates within the request list itself
        if (emailsInRequest.size() < employeeRequestDTOList.size()) {
            throw new ConflictException("The request contains duplicate emails");
        }
        //Check which of the requested emails already exist in the database with a single query.
        List<Employee> existingEmployees = employeeRepository.findByEmailIn(emailsInRequest);
        if(!existingEmployees.isEmpty()){
            String existingEmails = existingEmployees.stream()
                    .map(Employee::getEmail)
                    .collect(Collectors.joining(","));
            throw new DuplicateEmployeeException("The following emails already exists in the database with this emails: " + existingEmails);
        }
        //If all the validation pass, map all DTOs to the Employee Entity.
        List<Employee> employeesToSave = employeeRequestDTOList.stream().map(employeeRequestDTO -> {
            Employee employee = new Employee();
            employee.setName(employeeRequestDTO.name());
            employee.setEmail(employeeRequestDTO.email());
            employee.setDepartment(employeeRequestDTO.department());
            return employee;
        }).toList();
        //Save all new employees in a single database transaction.
        List<Employee> savedEmployees = employeeRepository.saveAll(employeesToSave);
        //Convert the saved entities to DTOs for the response.
        return savedEmployees.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public EmployeeResponseDTO getEmployeeById(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        MessageConstant.EMPLOYEE,"id", employeeId));
        return convertToDto(employee);
    }

    @Override
    public EmployeeResponseDTO getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        MessageConstant.EMPLOYEE, "email", email));
        return convertToDto(employee);
    }

    @Override
    public List<EmployeeResponseDTO> getEmployeesByDepartment(String department) {
        List<Employee> employees = employeeRepository.findByDepartmentIgnoreCase(department);
        return employees.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Long employeeId, EmployeeUpdateRequestDTO updateDTO) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        MessageConstant.EMPLOYEE, "id", employeeId));
        //Check if email is being updated and if it already exists
        if (updateDTO.email() != null && !updateDTO.email().equals(employee.getEmail()) && employeeRepository.existsByEmail(updateDTO.email())) {
            throw new ConflictException("An employee with this email" + updateDTO.email() + "already exists");
        }
        //Update Fields if provided
        if (updateDTO.name() != null) {
            employee.setName(updateDTO.name());
        }

        if (updateDTO.email() != null){
            employee.setEmail(updateDTO.email());
        }

        if (updateDTO.department() != null) {
            employee.setDepartment(updateDTO.department());
        }
        Employee updatedEmployee = employeeRepository.save(employee);
        return convertToDto(updatedEmployee);
    }


    @Override
    public void deleteEmployee(Long employeeId) {
        // Check if the employee exists before deleting
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFoundException(MessageConstant.EMPLOYEE,"id", employeeId);
        }
        employeeRepository.deleteById(employeeId);
    }


    private EmployeeResponseDTO convertToDto(Employee employee) {
        return new EmployeeResponseDTO(
                employee.getEmployeeId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
}