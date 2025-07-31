package com.example.inventorymanagementsystem.repository;

import com.example.inventorymanagementsystem.model.ResourceStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceStatusHistoryRepository extends JpaRepository<ResourceStatusHistory, Long> {
    
    // Find status history by resource ID
    List<ResourceStatusHistory> findByResourceIdOrderByChangedAtDesc(Long resourceId);
    
    // Find status history by resource code
    List<ResourceStatusHistory> findByResourceCodeOrderByChangedAtDesc(String resourceCode);
    
    // Find status history by new status
    List<ResourceStatusHistory> findByNewStatusOrderByChangedAtDesc(String newStatus);
    
    // Find status history by changed by user
    List<ResourceStatusHistory> findByChangedByOrderByChangedAtDesc(String changedBy);
} 