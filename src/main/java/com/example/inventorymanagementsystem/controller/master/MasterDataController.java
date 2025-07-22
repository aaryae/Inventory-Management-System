package com.example.inventorymanagementsystem.controller.master;

import com.example.inventorymanagementsystem.dtos.request.resource.ResourceClassRequestDTO;
import com.example.inventorymanagementsystem.dtos.request.resource.ResourceStatusRequestDTO;
import com.example.inventorymanagementsystem.dtos.request.resource.ResourceTypeRequestDTO;
import com.example.inventorymanagementsystem.dtos.response.ApiResponse;
import com.example.inventorymanagementsystem.dtos.response.resource.ResourceClassResponseDTO;
import com.example.inventorymanagementsystem.dtos.response.resource.ResourceTypeResponseDTO;
import com.example.inventorymanagementsystem.helper.MessageConstant;
import com.example.inventorymanagementsystem.model.ResourceClass;
import com.example.inventorymanagementsystem.model.ResourceStatus;
import com.example.inventorymanagementsystem.model.ResourceType;
import com.example.inventorymanagementsystem.service.MasterDataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master")
@Tag(name = "Master APIs", description = "meta data for resource")

public class MasterDataController {
    private final MasterDataService masterDataService;

    public MasterDataController (MasterDataService masterDataService){
        this.masterDataService = masterDataService;
    }

    @GetMapping("/resource-type")
    public ResponseEntity<ApiResponse> getAllResourceTypes(){
        List<ResourceTypeResponseDTO> types = masterDataService.getAllResourceTypes();
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, types));
    }

    @GetMapping("/resource-type/{resourceId}")
    public ResponseEntity<ApiResponse> getResourceTypeById(@PathVariable("resourceId") Long resourceId){
        ResourceType type = masterDataService.getResourceTypeById(resourceId);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, type));
    }

    @GetMapping("/resource-class")
    public ResponseEntity<ApiResponse> getAllResourceClass(){
        List<ResourceClassResponseDTO> classes = masterDataService.getAllResourceClass();
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, classes));
    }

    @GetMapping("/resource-class/{resourceId}")
    public ResponseEntity<ApiResponse> getResourceClassById(@PathVariable("resourceId") Long resourceId){
        ResourceClass resourceClass = masterDataService.getResourceClassById(resourceId);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, resourceClass));
    }

    @GetMapping("/resource-status")
    public ResponseEntity<ApiResponse> getAllResourceStatus(){
        List<ResourceStatus> statusList = masterDataService.getAllResourceStatus();
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, statusList));
    }

    @GetMapping("/resource-status/{resourceId}")
    public ResponseEntity<ApiResponse> getResourceStatusById(@PathVariable("resourceId") Long resourceId){
        ResourceStatus status = masterDataService.getResourceStatusById(resourceId);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, status));
    }

    @PostMapping("/resource-type")
    public ResponseEntity<ApiResponse> createResourceType(@RequestBody ResourceTypeRequestDTO dto){
        ResourceTypeResponseDTO resourceType = masterDataService.createResourceType(dto);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_ADDED, true, resourceType));
    }

    @PostMapping("/resource-class")
    public ResponseEntity<ApiResponse> createResourceClass(@RequestBody ResourceClassRequestDTO dto){
        ResourceClass resourceClass = masterDataService.createResourceClass(dto);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_ADDED, true, resourceClass));
    }

    @PostMapping("/resource-status")
    public ResponseEntity<ApiResponse> createResourceStatus(@RequestBody ResourceStatusRequestDTO dto){
        ResourceStatus resourceStatus = masterDataService.createResourceStatus(dto);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_ADDED, true, resourceStatus));
    }
}
