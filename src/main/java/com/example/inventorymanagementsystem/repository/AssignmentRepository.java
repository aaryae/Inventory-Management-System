package com.example.inventorymanagementsystem.repository;

import com.example.inventorymanagementsystem.model.Assignment;
import com.example.inventorymanagementsystem.model.Employee;
import com.example.inventorymanagementsystem.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long>, JpaSpecificationExecutor<Assignment>{

    //Find active assignments by employee
    List<Assignment> findAllByEmployeeAndIsActiveTrue(Employee employee);

    //Find active assignments by resource
    List<Assignment> findByResourceAndIsActiveTrue(Resource resource);

    //Find active assignments for specific resource
    //Optional<Assignment> findByResourceAndIsActiveTrueAndStatus(Resource resource, Assignment.AssignmentStatus status);

    //Find all active assignments
    List<Assignment> findByIsActiveTrue();

    //Find assignments by employee ID
    List<Assignment> findByEmployee_EmployeeId(Long employeeId);

    //Find active assignments by employee ID
    List<Assignment> findByEmployee_EmployeeIdAndIsActiveTrue(Long employeeId);

    //Find assignments by resource ID
    List<Assignment> findByResource_ResourceId(Long resourceResourceId);


    // Find active assignments by resource ID
    List<Assignment> findByResource_ResourceIdAndIsActiveTrue(Long resourceId);

    // Find assignments by status
    //List<Assignment> findByStatus(Assignment.AssignmentStatus status);

    // Find assignments by status and active
    //List<Assignment> findByStatusAndIsActiveTrue(Assignment.AssignmentStatus status);

    //
    }

