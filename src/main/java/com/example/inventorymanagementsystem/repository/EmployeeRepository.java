package com.example.inventorymanagementsystem.repository;

import com.example.inventorymanagementsystem.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    //Find employee by email
    Optional<Employee>findByEmail(String email);

    //Checks if email exists
    boolean existsByEmail(String email);

    //Find employees by department
    List<Employee> findByDepartmentIgnoreCase(String department);

    //Method for Bulk Validation
    List<Employee> findByEmailIn(Collection<String> emails);
}
