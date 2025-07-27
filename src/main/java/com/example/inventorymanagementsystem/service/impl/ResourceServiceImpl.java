package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.dtos.ResourceUpdateDTO;
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

                // Validation if resourceType matches with the batch resourceType requirement
                String incomingType = dto.resourceTypeName().trim().toLowerCase();
                String batchType = batch.getType().getResourceTypeName().trim().toLowerCase();
                if (!incomingType.equals(batchType)){
                    throw new InvalidBatchException("Resource type '" + incomingType + "' does not meet batch's type '" + batchType + "'.");
                }

                int currentCount = resourceRepository.countByBatch(batch);
                int incomingCount = requestDTOList.size();
                int totalAfterAddition = currentCount + incomingCount;

                if (totalAfterAddition > batch.getQuantity()) {
                    throw new BatchLimitException("Cannot add " + incomingCount + " resources. Batch capacity of " + batch.getQuantity() +
                            " would be exceeded (Currently " + currentCount + ").");
                } else if (totalAfterAddition < batch.getQuantity()) {
                    throw new BatchLimitException("Batch must be filled exactly with " + batch.getQuantity() +
                            ", but you are adding " + totalAfterAddition + " resources.");
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
                        getLong(row.getCell(8)) // batchId (nullable)
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
