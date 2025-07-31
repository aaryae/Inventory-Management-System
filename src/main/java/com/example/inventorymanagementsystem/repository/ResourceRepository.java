package com.example.inventorymanagementsystem.repository;

import com.example.inventorymanagementsystem.model.Batch;
import com.example.inventorymanagementsystem.model.ResourceStatus;
import com.example.inventorymanagementsystem.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> , JpaSpecificationExecutor<Resource> {
    int countByBatch(Batch batch);

    List<Resource> findByResourceStatus(ResourceStatus resourceStatus);

    List<Resource> findByBatch(Batch batch);

    @Query("SELECT r.brand, COUNT(r) FROM Resource r GROUP BY r.brand")
    List<Object[]> countByBrand();

    @Query("SELECT r.model, COUNT(r) FROM Resource r GROUP BY r.model")
    List<Object[]> countByModel();

    @Query("SELECT r.specification, COUNT(r) FROM Resource r GROUP BY r.specification")
    List<Object[]> countBySpecification();

    boolean existsByResourceCode(String resourceCode);
}