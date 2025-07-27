package com.example.inventorymanagementsystem.dtos.request.resource;

import jakarta.validation.constraints.NotBlank;

public record ResourceTypeRequestDTO(
        @NotBlank(message = "Please enter type name!")
        String resourceTypeName,

        @NotBlank(message = "Class name is required!")
        String resourceClassName
){
}
