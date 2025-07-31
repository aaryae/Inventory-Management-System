package com.example.inventorymanagementsystem.dtos.request.resource;

import java.time.LocalDate;

public record ResourceUpdateRequestDTO(
        String brand,
        String model,
        String specification,
        LocalDate purchaseDate,
        LocalDate warrantyExpiry,
        String resourceStatusName
) {
} 