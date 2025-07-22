package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.dtos.ResourceUpdateDTO;
import com.example.inventorymanagementsystem.dtos.request.resource.ResourceRequestDTO;
import com.example.inventorymanagementsystem.dtos.response.resource.ResourceResponseDTO;
import com.example.inventorymanagementsystem.exception.BarcodeGenerationException;
import com.example.inventorymanagementsystem.exception.BatchLimitExceedException;
import com.example.inventorymanagementsystem.exception.InvalidBatchException;
import com.example.inventorymanagementsystem.exception.ResourceNotFoundExceptionHandler;
import com.example.inventorymanagementsystem.helper.BarcodeGenerator;
import com.example.inventorymanagementsystem.helper.MessageConstant;
import com.example.inventorymanagementsystem.model.*;
import com.example.inventorymanagementsystem.repository.BatchRepository;
import com.example.inventorymanagementsystem.repository.ResourceRepository;
import com.example.inventorymanagementsystem.service.MasterDataService;
import com.example.inventorymanagementsystem.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Service
public class ResourceServiceImpl implements ResourceService {


    private final ResourceRepository resourceRepository;

    private final MasterDataService masterDataService;
    private final BatchRepository batchRepository;

    @Autowired
    public ResourceServiceImpl(ResourceRepository resourceRepository, MasterDataService masterDataService, BatchRepository batchRepository) {
        this.resourceRepository = resourceRepository;
        this.masterDataService = masterDataService;
        this.batchRepository = batchRepository;
    }


    @Override
    public List<ResourceResponseDTO> createResources(List<ResourceRequestDTO> requestDTOList) {

        List<Resource> resourceToSave = new ArrayList<>();

        if (requestDTOList.size() > 1 && requestDTOList.getFirst().batchId() == null) {
            throw new InvalidBatchException("Cannot add multiple resources without assigning a batchId.");
        }


        for (ResourceRequestDTO dto : requestDTOList) {
            //Validation of master data
            ResourceType type = masterDataService.getResourceTypeByName(requestDTOList.getFirst().resourceTypeName());
            ResourceClass resourceClass = masterDataService.getResourceClassByName(requestDTOList.getFirst().resourceClassName());
            ResourceStatus status = masterDataService.getResourceStatusByName(requestDTOList.getFirst().resourceStatusName());



            // Fetching the batch
            Batch batch = null;
            if (dto.batchId() != null) {
                batch = batchRepository.findById(dto.batchId())
                        .orElseThrow(() -> new ResourceNotFoundExceptionHandler(MessageConstant.BATCH, "id", dto.batchId()));

                int currentCount = resourceRepository.countByBatch(batch);
                int incomingCount = requestDTOList.size();

                if ((currentCount + incomingCount) > batch.getQuantity()) {
                    throw new BatchLimitExceedException("Cannot add " + incomingCount + " resources. Batch capacity of " + batch.getQuantity() +
                            " would be exceeded (Currently " + currentCount + ").");
                }
            }


            // Generating the resource code
            String resourceCode = generateUniqueResourceCode(type.getResourceTypeName());

            // Creating resource entity
            Resource resource = new Resource();
            resource.setBrand(dto.brand());
            resource.setModel(dto.model());
            resource.setSpecification(dto.specification());
            resource.setPurchaseDate(dto.purchaseDate());
            resource.setWarrantyExpiry(dto.warrantyExpiry());
            resource.setResourceCode(resourceCode);
            resource.setType(type);
            resource.setResourceClass(resourceClass);
            resource.setResourceStatus(status);
            resource.setBatch(batch);

            resourceToSave.add(resource);
        }

        // Saving all in the repository
        List<Resource> savedResources = resourceRepository.saveAll(resourceToSave);

        // Map to the ResponseDTO
        return savedResources.stream().map(this::convertToDto).toList();
    }

    @Override
    public ResourceResponseDTO getResourceById(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundExceptionHandler(MessageConstant.RESOURCE, "id", resourceId));

        return convertToDto(resource);
    }

    @Override
    public List<ResourceResponseDTO> getAllResources() {
        List<Resource> resources = resourceRepository.findAll();

        return resources.stream().map(this::convertToDto).toList();
    }

    @Override
    public List<ResourceResponseDTO> getResourcesByStatus(Long statusId) {
        // Validates if the status exists
        ResourceStatus status = masterDataService.getResourceStatusById(statusId);

        // Fetch all resources with this status
        List<Resource> resources = resourceRepository.findByResourceStatus(status);

        // Converts to response dto
        return resources.stream().map(this::convertToDto).toList();

    }

    @Override
    public ResourceResponseDTO updateResource(Long resourceId, ResourceUpdateDTO updateDTO) {
        // Fetches the existing resource
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundExceptionHandler(MessageConstant.RESOURCE, "id", resourceId));


        // Validation of new status
        ResourceStatus status = masterDataService.getResourceStatusByName(updateDTO.resourceStatusName());

        resource.setModel(updateDTO.model());
        resource.setBrand(updateDTO.brand());
        resource.setSpecification(updateDTO.specification());
        resource.setPurchaseDate(updateDTO.purchaseDate());
        resource.setWarrantyExpiry(updateDTO.warrantyExpiry());
        resource.setResourceStatus(status);

        // Save and update the resources
        Resource updated = resourceRepository.save(resource);

        // Mapping to response DTO
        return convertToDto(updated);
    }

    @Override
    public void deleteResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundExceptionHandler(MessageConstant.RESOURCE, "id", resourceId));
        resourceRepository.delete(resource);
    }

    @Override
    public String generateBarcode(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundExceptionHandler(MessageConstant.RESOURCE, "id", resourceId));

        try {
            byte[] barcodeBytes = BarcodeGenerator.generateBarcodeImage(String.valueOf(resource.getResourceId()), 300, 100);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(barcodeBytes);
        }catch (Exception e){
            throw new BarcodeGenerationException(MessageConstant.BARCODE_GENERATION_FAILED + e.getMessage());
        }
    }

    private static final Random r = new Random();
    public String generateUniqueResourceCode(String typePrefix) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = r.nextInt(999);
        return typePrefix.toUpperCase() + "-" + date + "-" + String.format("%03d", random);
    }

    private ResourceResponseDTO convertToDto(Resource resource) {
        return new ResourceResponseDTO(
                resource.getResourceId(),
                resource.getResourceCode(),
                resource.getBrand(),
                resource.getModel(),
                resource.getSpecification(),
                resource.getPurchaseDate(),
                resource.getWarrantyExpiry(),
                resource.getType().getResourceTypeName(),
                resource.getResourceClass().getResourceClassName(),
                resource.getResourceStatus().getResourceStatusName(),
                resource.getBatch() != null ? resource.getBatch().getBatchCode() : null,
                resource.getCreatedAt(),
                resource.getUpdatedAt()
        );
    }


}
