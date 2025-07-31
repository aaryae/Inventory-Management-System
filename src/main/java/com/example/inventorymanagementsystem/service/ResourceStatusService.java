package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.dtos.request.resource.ResourceStatusUpdateDTO;
import com.example.inventorymanagementsystem.dtos.response.resource.ResourceResponseDTO;
import com.example.inventorymanagementsystem.dtos.response.resource.ResourceStatusHistoryDTO;

import java.util.List;

public interface ResourceStatusService {
    
    // Update resource status
    ResourceResponseDTO updateResourceStatus(ResourceStatusUpdateDTO updateDTO);
    
    // Get status history for a resource
    List<ResourceStatusHistoryDTO> getStatusHistoryByResourceId(Long resourceId);
    
    // Get status history for a resource by code
    List<ResourceStatusHistoryDTO> getStatusHistoryByResourceCode(String resourceCode);
    
    // Get all resources by status
    List<ResourceResponseDTO> getResourcesByStatus(String statusName);
    
    // Get status history by status
    List<ResourceStatusHistoryDTO> getStatusHistoryByStatus(String statusName);
    
    // Get status history by user
    List<ResourceStatusHistoryDTO> getStatusHistoryByUser(String username);
    
    // Bulk status update
    List<ResourceResponseDTO> bulkUpdateStatus(List<ResourceStatusUpdateDTO> updateDTOs);
    
    // Get available statuses
    List<String> getAvailableStatuses();
} 