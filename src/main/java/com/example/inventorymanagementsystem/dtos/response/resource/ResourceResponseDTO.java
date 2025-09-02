package com.example.inventorymanagementsystem.dtos.response.resource;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ResourceResponseDTO(
        Long resourceId,
        String resourceCode,
        String brand,
        String model,
        String specification,
        LocalDate purchaseDate,
        LocalDate warrantyExpiry,
        String resourceType,
        String resourceClass,
        String resourceStatus,
        String batchCode,
        Double unitPrice,
        String serialNumber,
        String remarks,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
