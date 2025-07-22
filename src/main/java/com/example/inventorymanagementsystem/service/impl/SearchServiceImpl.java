package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.dtos.response.resource.ResourceResponseDTO;
import com.example.inventorymanagementsystem.model.Resource;
import com.example.inventorymanagementsystem.repository.ResourceRepository;
import com.example.inventorymanagementsystem.repository.ResourceSpecifications;
import com.example.inventorymanagementsystem.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ResourceRepository resourceRepository;

    @Override
    public List<ResourceResponseDTO> filterResources(String brand, String model, LocalDate purchaseDate, String specification) {

        Specification<Resource> spec = (root, query, cb) -> cb.conjunction();

        if (brand != null) {
            spec = spec.and(ResourceSpecifications.brandContains(brand));
        }
        if (model != null) {
            spec = spec.and(ResourceSpecifications.modelContains(model));
        }
        if (purchaseDate != null) {
            spec = spec.and(ResourceSpecifications.purchasedAfter(purchaseDate));
        }
        if (specification != null) {
            spec = spec.and(ResourceSpecifications.typeEquals(specification));
        }

        List<Resource> resources = resourceRepository.findAll(spec);

        return resources.stream()
                .map(resource -> ResourceResponseDTO.builder()
                        .resourceId(resource.getResourceId())
                        .resourceCode(resource.getResourceCode())
                        .brand(resource.getBrand())
                        .model(resource.getModel())
                        .specification(resource.getSpecification())
                        .purchaseDate(resource.getPurchaseDate())
                        .warrantyExpiry(resource.getWarrantyExpiry())
                        .resourceType(resource.getType().getResourceTypeName())
                        .resourceClass(resource.getResourceClass().getResourceClassName())
                        .resourceStatus(resource.getResourceStatus().getResourceStatusName())
                        .batchCode(resource.getBatch() != null ? resource.getBatch().getBatchCode() : null)
                        .createdAt(resource.getCreatedAt())
                        .updatedAt(resource.getUpdatedAt())
                        .build())
                .toList();
    }


}







