package com.example.inventorymanagementsystem.service;

import com.example.inventorymanagementsystem.dtos.response.resource.ResourceResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface SearchService {

    public List<ResourceResponseDTO> filterResources(String brand, String model, LocalDate purchaseDate, String specification);
}
