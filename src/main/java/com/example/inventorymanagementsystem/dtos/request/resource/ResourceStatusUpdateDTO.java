package com.example.inventorymanagementsystem.dtos.request.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResourceStatusUpdateDTO(
    @NotNull(message = "Resource ID is required")
    Long resourceId,
    
    @NotBlank(message = "Status name is required")
    String statusName,
    
    String statusChangeReason,
    
    String notes
) {} 