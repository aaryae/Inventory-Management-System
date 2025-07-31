package com.example.inventorymanagementsystem.dtos.response.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ResourceStatusHistoryDTO(
    Long historyId,
    Long resourceId,
    String resourceCode,
    String oldStatus,
    String newStatus,
    String changeReason,
    String notes,
    String changedBy,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime changedAt
) {} 