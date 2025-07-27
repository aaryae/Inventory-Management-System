package com.example.inventorymanagementsystem.controller.resource;

import com.beust.ah.A;
import com.example.inventorymanagementsystem.dtos.ResourceUpdateDTO;
import com.example.inventorymanagementsystem.dtos.request.resource.ResourceRequestDTO;
import com.example.inventorymanagementsystem.dtos.response.ApiResponse;
import com.example.inventorymanagementsystem.dtos.response.resource.ResourceResponseDTO;
import com.example.inventorymanagementsystem.helper.MessageConstant;
import com.example.inventorymanagementsystem.service.ResourceService;
import com.example.inventorymanagementsystem.service.impl.ResourceServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@Tag(name = "Resource APIs", description = "crud for resources")

public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceServiceImpl resourceService){
        this.resourceService = resourceService;
    }


    @PostMapping
    public ResponseEntity<ApiResponse> createResources(@RequestBody List<ResourceRequestDTO> requestDTOList){
        List<ResourceResponseDTO> responseDTOList = resourceService.createResources(requestDTOList);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_ADDED, true, responseDTOList));
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<ApiResponse> getResourceById(@PathVariable("resourceId") Long resourceId){
        ResourceResponseDTO responseDTO = resourceService.getResourceById(resourceId);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, responseDTO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllResources(){
        List<ResourceResponseDTO> responseDTOList= resourceService.getAllResources();
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, responseDTOList));
    }

    @GetMapping("/status/{statusId}")
    public ResponseEntity<ApiResponse> getByStatusId(@PathVariable("statusId") Long statusId){
        List<ResourceResponseDTO> responseDTOList = resourceService.getResourcesByStatus(statusId);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, responseDTOList));
    }

    @PutMapping("/{resourceId}")
    public ResponseEntity<ApiResponse> updateResource(@PathVariable("resourceId") Long resourceId, @RequestBody ResourceUpdateDTO resourceUpdate){
        ResourceResponseDTO responseDTO = resourceService.updateResource(resourceId, resourceUpdate);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_UPDATED, true, responseDTO));
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<ApiResponse> deleteResource(@PathVariable("resourceId") Long resourceId){
        resourceService.deleteResource(resourceId);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_DELETED, true));
    }

    @GetMapping("/{resourceId}/barcode")
    public ResponseEntity<ApiResponse> getBarcode(@PathVariable("resourceId") Long resourceId){
        String barcodeBase64 = resourceService.generateBarcode(resourceId);
        return ResponseEntity.ok().body(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, barcodeBase64));
    }

}
