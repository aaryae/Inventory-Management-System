package com.example.inventorymanagementsystem.controller.batch;

import com.example.inventorymanagementsystem.dtos.request.BatchRequestDTO;
import com.example.inventorymanagementsystem.dtos.response.ApiResponse;
import com.example.inventorymanagementsystem.dtos.response.resource.BatchResponseDTO;
import com.example.inventorymanagementsystem.dtos.response.resource.ResourceResponseDTO;
import com.example.inventorymanagementsystem.helper.MessageConstant;
import com.example.inventorymanagementsystem.service.BatchService;
import com.example.inventorymanagementsystem.service.impl.BatchServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
@Tag(name = "Batch APIs", description = "APIs for batch")

public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchServiceImpl batchService){
        this.batchService = batchService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createBatch(@RequestBody BatchRequestDTO requestDTO){
        BatchResponseDTO responseDTO = batchService.createBatch(requestDTO);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_ADDED, true, responseDTO));
    }

    @GetMapping("/{batchId}")
    public ResponseEntity<ApiResponse> getBatchById(@PathVariable("batchId") Long batchId){
        BatchResponseDTO responseDTO = batchService.getBatchById(batchId);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, responseDTO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllBatches(){
        List<BatchResponseDTO> responseDTOList = batchService.getAllBatches();
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, responseDTOList));
    }

    @GetMapping("/{batchId}/resources")
    public ResponseEntity<ApiResponse> getResourcesByBatch(@PathVariable Long batchId){
        List<ResourceResponseDTO> responseDTOList = batchService.getResourcesByBatchId(batchId);
        return ResponseEntity.ok(new ApiResponse(MessageConstant.SUCCESSFULLY_FETCHED, true, responseDTOList));
    }
}
