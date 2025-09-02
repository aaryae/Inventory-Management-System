package com.example.inventorymanagementsystem.service.security;

import com.example.inventorymanagementsystem.model.User;
import io.jsonwebtoken.Claims;

public interface JwtService {
    String buildToken(String subject, long expirationTime, String tokenType);
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    boolean isAccessToken(String token);
    boolean isRefreshToken(String token);
    Claims extractAllClaims(String token);
     String extractUsername(String token);

}
