package com.example.inventorymanagementsystem.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "resources")
public class Resource {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceId;

    @Column(name = "resourceCode", unique = true, nullable = false)
    private String resourceCode;

    @Size(min = 2, max = 10)
    @Column(name = "brand", nullable = false, columnDefinition = "text")
    private String brand;

    @Size(min = 2, max = 15)
    @Column(name = "model", nullable = false, columnDefinition = "text")
    private String model;

    @Size(min = 5, max = 50)
    @Column(name = "specification", nullable = false, columnDefinition = "text")
    private String specification;

    @Column(name = "purchaseDate", nullable = false, columnDefinition = "text")
    private LocalDate purchaseDate;

    @Column(name = "warrantyExpiry", columnDefinition = "text")
    private LocalDate warrantyExpiry;

    @CreatedDate
    @Column(name = "createdAt", updatable = false, columnDefinition = "timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updatedAt", updatable = false, columnDefinition = "timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    // Foreign Keys
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resourceStatusId", nullable = false, columnDefinition = "bigint")
    private ResourceStatus resourceStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resourceTypeId", nullable = false, columnDefinition = "bigint")
    private ResourceType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resourceClassId", nullable = false, columnDefinition = "bigint")
    private ResourceClass resourceClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resourceBatchId", columnDefinition = "bigint")
    private Batch batch;
}
