package com.example.inventorymanagementsystem.dtos.request.resource;

import jakarta.validation.constraints.NotBlank;

public record ResourceClassRequestDTO(
        @NotBlank(message = "Please enter class name!")
        String className
) {
}
