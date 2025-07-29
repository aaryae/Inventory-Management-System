package com.example.inventorymanagementsystem.dtos.request.security;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Builder
public record RefreshTokenRequest(@NotNull String refreshToken) {

}
