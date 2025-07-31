package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.dtos.request.resource.ResourceStatusUpdateDTO;
import com.example.inventorymanagementsystem.dtos.response.resource.ResourceResponseDTO;
import com.example.inventorymanagementsystem.dtos.response.resource.ResourceStatusHistoryDTO;
import com.example.inventorymanagementsystem.exception.*;
import com.example.inventorymanagementsystem.helper.MessageConstant;
import com.example.inventorymanagementsystem.helper.ResourceStatusEnum;
import com.example.inventorymanagementsystem.model.Resource;
import com.example.inventorymanagementsystem.model.ResourceStatus;
import com.example.inventorymanagementsystem.model.ResourceStatusHistory;
import com.example.inventorymanagementsystem.repository.ResourceRepository;
import com.example.inventorymanagementsystem.repository.ResourceStatusHistoryRepository;
import com.example.inventorymanagementsystem.repository.ResourceStatusRepository;
import com.example.inventorymanagementsystem.service.ResourceStatusService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceStatusServiceImpl implements ResourceStatusService {

    private final ResourceRepository resourceRepository;
    private final ResourceStatusRepository resourceStatusRepository;
    private final ResourceStatusHistoryRepository statusHistoryRepository;

    @Autowired
    public ResourceStatusServiceImpl(ResourceRepository resourceRepository,
                                  ResourceStatusRepository resourceStatusRepository,
                                  ResourceStatusHistoryRepository statusHistoryRepository) {
        this.resourceRepository = resourceRepository;
        this.resourceStatusRepository = resourceStatusRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    @Override
    @Transactional
    public ResourceResponseDTO updateResourceStatus(ResourceStatusUpdateDTO updateDTO) {
        // Validate resource exists
        Resource resource = resourceRepository.findById(updateDTO.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.RESOURCE, "id", updateDTO.resourceId()));

        // Validate status exists
        ResourceStatus newStatus = resourceStatusRepository.findByResourceStatusNameIgnoreCase(updateDTO.statusName())
                .orElseThrow(() -> new DataNotFoundException("Status not found: " + updateDTO.statusName()));

        // Validate status transition
        validateStatusTransition(resource.getResourceStatus().getResourceStatusName(), updateDTO.statusName());

        // Store old status for history
        String oldStatus = resource.getResourceStatus().getResourceStatusName();

        // Update resource status
        resource.setResourceStatus(newStatus);
        Resource updatedResource = resourceRepository.save(resource);

        // Create status history record
        createStatusHistoryRecord(resource, oldStatus, updateDTO.statusName(), 
                updateDTO.statusChangeReason(), updateDTO.notes());

        return convertToResponseDTO(updatedResource);
    }

    @Override
    public List<ResourceStatusHistoryDTO> getStatusHistoryByResourceId(Long resourceId) {
        List<ResourceStatusHistory> history = statusHistoryRepository.findByResourceIdOrderByChangedAtDesc(resourceId);
        return history.stream()
                .map(this::convertToHistoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceStatusHistoryDTO> getStatusHistoryByResourceCode(String resourceCode) {
        List<ResourceStatusHistory> history = statusHistoryRepository.findByResourceCodeOrderByChangedAtDesc(resourceCode);
        return history.stream()
                .map(this::convertToHistoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceResponseDTO> getResourcesByStatus(String statusName) {
        ResourceStatus status = resourceStatusRepository.findByResourceStatusNameIgnoreCase(statusName)
                .orElseThrow(() -> new DataNotFoundException("Status not found: " + statusName));

        List<Resource> resources = resourceRepository.findByResourceStatus(status);
        return resources.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceStatusHistoryDTO> getStatusHistoryByStatus(String statusName) {
        List<ResourceStatusHistory> history = statusHistoryRepository.findByNewStatusOrderByChangedAtDesc(statusName);
        return history.stream()
                .map(this::convertToHistoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceStatusHistoryDTO> getStatusHistoryByUser(String username) {
        List<ResourceStatusHistory> history = statusHistoryRepository.findByChangedByOrderByChangedAtDesc(username);
        return history.stream()
                .map(this::convertToHistoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ResourceResponseDTO> bulkUpdateStatus(List<ResourceStatusUpdateDTO> updateDTOs) {
        return updateDTOs.stream()
                .map(this::updateResourceStatus)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAvailableStatuses() {
        return Arrays.stream(ResourceStatusEnum.values())
                .map(ResourceStatusEnum::getDisplayName)
                .collect(Collectors.toList());
    }

    private void validateStatusTransition(String oldStatus, String newStatus) {
        // Define valid status transitions
        if (oldStatus.equalsIgnoreCase("Assigned") && newStatus.equalsIgnoreCase("Available")) {
            throw new BusinessLogicException("Cannot change status from Assigned to Available. Return the resource first.", 
                    false, null, org.springframework.http.HttpStatus.CONFLICT);
        }

        if (oldStatus.equalsIgnoreCase("Lost") && !newStatus.equalsIgnoreCase("Available")) {
            throw new BusinessLogicException("Lost resources can only be changed to Available status.", 
                    false, null, org.springframework.http.HttpStatus.CONFLICT);
        }

        if (oldStatus.equalsIgnoreCase("Damaged") && !newStatus.equalsIgnoreCase("InRepair")) {
            throw new BusinessLogicException("Damaged resources can only be changed to InRepair status.", 
                    false, null, org.springframework.http.HttpStatus.CONFLICT);
        }
    }

    private void createStatusHistoryRecord(Resource resource, String oldStatus, String newStatus, 
                                        String changeReason, String notes) {
        ResourceStatusHistory history = new ResourceStatusHistory();
        history.setResourceId(resource.getResourceId());
        history.setResourceCode(resource.getResourceCode());
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangeReason(changeReason);
        history.setNotes(notes);
        history.setChangedBy(getCurrentUsername());
        history.setChangedAt(LocalDateTime.now());

        statusHistoryRepository.save(history);
    }

    private ResourceResponseDTO convertToResponseDTO(Resource resource) {
        return ResourceResponseDTO.builder()
                .resourceId(resource.getResourceId())
                .resourceCode(resource.getResourceCode())
                .brand(resource.getBrand())
                .model(resource.getModel())
                .specification(resource.getSpecification())
                .purchaseDate(resource.getPurchaseDate())
                .warrantyExpiry(resource.getWarrantyExpiry())
                .resourceType(resource.getType().getResourceTypeName())
                .resourceClass(resource.getResourceClass().getResourceClassName())
                .resourceStatus(resource.getResourceStatus().getResourceStatusName())
                .batchCode(resource.getBatch() != null ? resource.getBatch().getBatchCode() : null)
                .unitPrice(resource.getUnitPrice())
                .serialNumber(resource.getSerialNumber())
                .remarks(resource.getRemarks())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();
    }

    private ResourceStatusHistoryDTO convertToHistoryDTO(ResourceStatusHistory history) {
        return new ResourceStatusHistoryDTO(
                history.getHistoryId(),
                history.getResourceId(),
                history.getResourceCode(),
                history.getOldStatus(),
                history.getNewStatus(),
                history.getChangeReason(),
                history.getNotes(),
                history.getChangedBy(),
                history.getChangedAt()
        );
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
} 