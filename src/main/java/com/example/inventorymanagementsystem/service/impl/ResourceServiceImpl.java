package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.dtos.request.resource.ResourceUpdateRequestDTO;
import com.example.inventorymanagementsystem.dtos.request.resource.ResourceRequestDTO;
import com.example.inventorymanagementsystem.dtos.response.resource.ResourceResponseDTO;
import com.example.inventorymanagementsystem.exception.*;
import com.example.inventorymanagementsystem.helper.BarcodeGenerator;
import com.example.inventorymanagementsystem.helper.MessageConstant;
import com.example.inventorymanagementsystem.model.*;
import com.example.inventorymanagementsystem.repository.BatchRepository;
import com.example.inventorymanagementsystem.repository.ResourceRepository;
import com.example.inventorymanagementsystem.service.MasterDataService;
import com.example.inventorymanagementsystem.service.ResourceService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

        if (requestDTOList.size() > 1 && requestDTOList.getFirst().batchId() == null) {
            throw new InvalidBatchException("Cannot add multiple resources without assigning a batchId.");
        }

        // Step 1: Group resources by batchId
        Map<Long, List<ResourceRequestDTO>> groupedByBatchId = requestDTOList.stream()
                .filter(resourceDTO -> resourceDTO.batchId() != null)
                .collect(Collectors.groupingBy(ResourceRequestDTO::batchId));

        // Step 2: Validate each batch group
        Set<String> generatedCodes = new HashSet<>();

        for (Map.Entry<Long, List<ResourceRequestDTO>> entry : groupedByBatchId.entrySet()) {
            Long batchId = entry.getKey();
            List<ResourceRequestDTO> group = entry.getValue();

            Batch batch = batchRepository.findById(batchId)
                    .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.BATCH, "id", batchId));

            String batchType = batch.getType().getResourceTypeName().trim().toLowerCase();

            // Ensure all resources in this batch match the expected type
            for (ResourceRequestDTO dto : group) {
                String incomingType = dto.resourceTypeName().trim().toLowerCase();
                if (!incomingType.equals(batchType)) {
                    throw new InvalidBatchException("Resource type '" + incomingType + "' does not match batch type '" + batchType + "' for batch ID " + batchId);
                }
            }

            int currentCount = resourceRepository.countByBatch(batch);
            int newCount = group.size();
            int total = currentCount + newCount;

            if (total > batch.getQuantity()) {
                throw new BatchLimitException("Cannot add " + newCount + " resources to batch " + batch.getBatchCode() +
                        ". Batch capacity of " + batch.getQuantity() + " would be exceeded (currently " + currentCount + ").");
            } else if (total < batch.getQuantity()) {
                throw new BatchLimitException("Batch '" + batch.getBatchCode() + "' must be filled exactly with " +
                        batch.getQuantity() + " items. You're adding " + total + ".");
            }
        }

        // Step 3: Convert DTOs to Resource entities
        List<Resource> resourceToSave = new ArrayList<>();

        for (ResourceRequestDTO dto : requestDTOList) {
            ResourceType type = masterDataService.getResourceTypeByName(dto.resourceTypeName());
            ResourceClass resourceClass = masterDataService.getResourceClassByName(dto.resourceClassName());
            ResourceStatus status = masterDataService.getResourceStatusByName(dto.resourceStatusName());

            Batch batch = null;
            if (dto.batchId() != null) {
                batch = batchRepository.findById(dto.batchId())
                        .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.BATCH, "id", dto.batchId()));
            }

            String resourceCode = generateUniqueResourceCode(type.getResourceTypeName(), generatedCodes);


            Resource resource = new Resource();
            resource.setBrand(dto.brand());
            resource.setModel(dto.model());
            resource.setSpecification(dto.specification());
            resource.setPurchaseDate(dto.purchaseDate());
            resource.setWarrantyExpiry(dto.warrantyExpiry());
            resource.setUnitPrice(dto.unitPrice());
            resource.setSerialNumber(dto.serialNumber());
            resource.setRemarks(dto.remarks());
            resource.setResourceCode(resourceCode);
            resource.setType(type);
            resource.setResourceClass(resourceClass);
            resource.setResourceStatus(status);
            resource.setBatch(batch);

            resourceToSave.add(resource);
        }

        List<Resource> savedResources = resourceRepository.saveAll(resourceToSave);
        return savedResources.stream().map(this::convertToDto).toList();
    }


    @Override
    public ResourceResponseDTO getResourceById(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.RESOURCE, "id", resourceId));

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
    public ResourceResponseDTO updateResource(Long resourceId, ResourceUpdateRequestDTO updateDTO) {
        // Fetches the existing resource
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.RESOURCE, "id", resourceId));


        if (updateDTO.model() != null) resource.setModel(updateDTO.model());
        if (updateDTO.brand() != null) resource.setBrand(updateDTO.brand());
        if (updateDTO.specification() != null) resource.setSpecification(updateDTO.specification());
        if (updateDTO.purchaseDate() != null) resource.setPurchaseDate(updateDTO.purchaseDate());
        if (updateDTO.warrantyExpiry() != null) resource.setWarrantyExpiry(updateDTO.warrantyExpiry());
        if (updateDTO.resourceStatusName() != null) {
            ResourceStatus status = masterDataService.getResourceStatusByName(updateDTO.resourceStatusName());
            resource.setResourceStatus(status);
        }

        // Save and update the resources
        Resource updated = resourceRepository.save(resource);

        // Mapping to response DTO
        return convertToDto(updated);
    }

    @Override
    public void deleteResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.RESOURCE, "id", resourceId));
        resourceRepository.delete(resource);
    }

    @Override
    public String generateBarcode(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.RESOURCE, "id", resourceId));

        try {
            byte[] barcodeBytes = BarcodeGenerator.generateBarcodeImage(String.valueOf(resource.getResourceId()), 300, 100);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(barcodeBytes);
        }catch (Exception e){
            throw new BarcodeGenerationException(MessageConstant.BARCODE_GENERATION_FAILED + e.getMessage());
        }
    }

    public List<ResourceRequestDTO> parseExcelToResources(MultipartFile file) {
        List<ResourceRequestDTO> resources = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Skip header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null || row.getCell(0) == null || getString(row.getCell(0)).isBlank()) continue;

                ResourceRequestDTO dto = new ResourceRequestDTO(
                        getString(row.getCell(0)), // brand
                        getString(row.getCell(1)), // model
                        getString(row.getCell(2)), // specification
                        getLocalDate(row.getCell(3)), // purchaseDate
                        getLocalDate(row.getCell(4)), // warrantyExpiry
                        getString(row.getCell(5)), // resourceTypeName
                        getString(row.getCell(6)), // resourceClassName
                        getString(row.getCell(7)), // resourceStatusName
                        getDouble(row.getCell(8)),  // unitPrice
                        getString(row.getCell(9)), // serialNumber
                        getString(row.getCell(10)), // remarks
                        getLong(row.getCell(11)) // batchId (nullable)
                );

                resources.add(dto);
            }
        } catch (Exception e) {
            throw new ExcelParsingException("Failed to parse Excel file: " + e.getMessage());
        }

        return resources;
    }

    // 1. Safely extract String value
    private String getString(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    // 2. Safely extract LocalDate from a date-formatted cell
    private LocalDate getLocalDate(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }

        return null;
    }

    // 3. Safely extract Long value from number or numeric string
    private Long getLong(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            return (long) cell.getNumericCellValue();
        }

        if (cell.getCellType() == CellType.STRING) {
            try {
                return Long.parseLong(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    private Double getDouble(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }


    private static final Random r = new Random();

    public String generateUniqueResourceCode(String typePrefix, Set<String> existingCodes) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String resourceCode;
        int attempts = 0;

        do {
            int random = r.nextInt(1000); // 000 to 999
            resourceCode = typePrefix.toUpperCase() + "-" + date + "-" + String.format("%03d", random);
            attempts++;

            if (attempts > 20) {
                throw new RuntimeException("Failed to generate a unique resource code after 20 attempts");
            }
        } while (existingCodes.contains(resourceCode) || resourceRepository.existsByResourceCode(resourceCode));

        existingCodes.add(resourceCode);
        return resourceCode;
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
                resource.getUnitPrice(),
                resource.getSerialNumber(),
                resource.getRemarks(),
                resource.getCreatedAt(),
                resource.getUpdatedAt()
        );
    }


}
