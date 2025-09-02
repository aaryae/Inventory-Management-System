package com.example.inventorymanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @NotBlank(message = "Employee name is required")
    @Size(min = 2, max = 100, message = "Employee name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(name ="email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Department is required")
    @Size(min = 2,max = 50, message = "Department must be between 2 and 50 characters")
    @Column(name = "department", nullable = false)
    private String department;

    @CreatedDate
    @Column(name = "createdAt", updatable = false, columnDefinition = "timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updatedAt", columnDefinition = "timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "employee",  cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Assignment> assignments;
}
