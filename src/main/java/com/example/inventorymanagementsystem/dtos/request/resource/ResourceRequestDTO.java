package com.example.inventorymanagementsystem.dtos.request.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ResourceRequestDTO(
        @NotBlank(message = "Brand must not be blank!")
        @Size(min = 2, max = 20)
        String brand,

        @NotBlank(message = "Model must not be blank!")
        @Size(min = 2, max = 20)
        String model,

        @NotBlank(message = "Specification must not be blank!")
        @Size(min = 5, max = 50)
        String specification,

        @NotNull(message = "Purchase date is required!")
        LocalDate purchaseDate,

        LocalDate warrantyExpiry,

        @NotBlank(message = "Resource type name is required!")
        String resourceTypeName,

        @NotBlank(message = "Resource class name is required!")
        String resourceClassName,

        @NotBlank(message = "Resource status name is required!")
        String resourceStatusName,

        @NotNull(message = "Unit price is required!")
        Double unitPrice,

        @NotBlank(message = "Serial number is required!")
        @Size(min = 2, max = 50)
        String serialNumber,

        @Size(min = 5, max = 60)
        String remarks,

        Long batchId
){
}
