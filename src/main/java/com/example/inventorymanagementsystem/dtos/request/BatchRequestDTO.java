package com.example.inventorymanagementsystem.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BatchRequestDTO(
        @NotBlank(message = "Type name is required!")
        String resourceTypeName,

        @NotNull(message = "Please enter quantity!")
        @Positive(message = "Quantity must be positive!")
        Integer quantity,

        @NotBlank(message = "Description is required!")
        String description
){
}
