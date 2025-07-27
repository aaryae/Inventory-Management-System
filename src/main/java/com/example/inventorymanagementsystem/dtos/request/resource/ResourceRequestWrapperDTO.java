package com.example.inventorymanagementsystem.dtos.request.resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ResourceRequestWrapperDTO(
        @NotEmpty(message = "Resource list cannot be empty!")
        @Valid List<ResourceRequestDTO> resources
) {}