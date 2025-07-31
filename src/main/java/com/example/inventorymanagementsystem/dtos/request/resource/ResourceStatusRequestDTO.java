package com.example.inventorymanagementsystem.dtos.request.resource;


import jakarta.validation.constraints.NotBlank;

public record ResourceStatusRequestDTO(
        @NotBlank(message = "Please enter status!")
        String statusName
){
}
