package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.dtos.request.BatchRequestDTO;
import com.example.inventorymanagementsystem.dtos.response.resource.BatchResponseDTO;
import com.example.inventorymanagementsystem.dtos.response.resource.ResourceResponseDTO;
import com.example.inventorymanagementsystem.exception.ResourceNotFoundExceptionHandler;
import com.example.inventorymanagementsystem.helper.MessageConstant;
import com.example.inventorymanagementsystem.model.Batch;
import com.example.inventorymanagementsystem.model.ResourceType;
import com.example.inventorymanagementsystem.model.Resource;
import com.example.inventorymanagementsystem.repository.BatchRepository;
import com.example.inventorymanagementsystem.repository.ResourceRepository;
import com.example.inventorymanagementsystem.service.BatchService;
import com.example.inventorymanagementsystem.service.MasterDataService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class BatchServiceImpl implements BatchService {


    private final BatchRepository batchRepository;
    private final MasterDataService masterDataService;
    private final ResourceRepository resourceRepository;

    public BatchServiceImpl(BatchRepository batchRepository, MasterDataService masterDataService, ResourceRepository resourceRepository){
        this.batchRepository = batchRepository;
        this.masterDataService = masterDataService;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public BatchResponseDTO createBatch(BatchRequestDTO batchRequestDTO) {
        // Validate the resource type
        ResourceType resourceType = masterDataService.getResourceTypeByName(batchRequestDTO.resourceTypeName());

        // Generation of batch code
        String batchCode = generateBatchCode(resourceType.getResourceTypeName());

        // Creating the batch entity
        Batch batch = new Batch();
        batch.setBatchCode(batchCode);
        batch.setQuantity(batchRequestDTO.quantity());
        batch.setDescription(batchRequestDTO.description());
        batch.setType(resourceType);

        // Saving the batch
        Batch saved =batchRepository.save(batch);

        // Map to the response DTO
        return new BatchResponseDTO(
                saved.getBatchId(),
                saved.getBatchCode(),
                saved.getQuantity(),
                saved.getDescription(),
                saved.getType().getResourceTypeName()
        );
    }

    @Override
    public BatchResponseDTO getBatchById(Long batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundExceptionHandler(MessageConstant.BATCH, "id", batchId));

        return new BatchResponseDTO(
                batch.getBatchId(),
                batch.getBatchCode(),
                batch.getQuantity(),
                batch.getDescription(),
                batch.getType().getResourceTypeName()
        );
    }

    @Override
    public List<BatchResponseDTO> getAllBatches() {
        List<Batch> batches = batchRepository.findAll();

        return batches.stream().map(batch -> new BatchResponseDTO(
                batch.getBatchId(),
                batch.getBatchCode(),
                batch.getQuantity(),
                batch.getDescription(),
                batch.getType().getResourceTypeName()
        )).toList();
    }

    @Override
    public List<ResourceResponseDTO> getResourcesByBatchId(Long batchId) {
        // Validation of batch Id
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundExceptionHandler(MessageConstant.BATCH, "id", batchId));

        // Fetches resources assigned to the batch
        List<Resource> resources = resourceRepository.findByBatch(batch);

        // Maps to response DTO
        return resources.stream().map(resource -> new ResourceResponseDTO(
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
            )).toList();
    }

    private static final Random r = new Random();

    private String generateBatchCode(String typePrefix){
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = r.nextInt(999);
        return typePrefix.toUpperCase() + "-BATCH-" + date + "-" + String.format("%03d", random);
    }
}
