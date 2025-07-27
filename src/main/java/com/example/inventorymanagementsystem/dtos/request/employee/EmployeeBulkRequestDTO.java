package com.example.inventorymanagementsystem.dtos.request.employee;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeBulkRequestDTO {
    @Valid
    private List<EmployeeRequestDTO> employees;
}
