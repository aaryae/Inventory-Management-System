package com.example.inventorymanagementsystem.service.security.impl;

import com.example.inventorymanagementsystem.model.User;
import com.example.inventorymanagementsystem.service.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;

@Service
public class JwtServiceImpl implements JwtService {


    private final String SECRET = "413F4428472B4B6250655368566D5970337336763979244226452948404D6351";
    private final Key jwtSigningKey = Keys.hmacShaKeyFor(hexStringToByteArray(SECRET));

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 10;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;

    @Override
    public String buildToken(String subject, long expirationTime, String tokenType) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("tokenType", tokenType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(jwtSigningKey, SignatureAlgorithm.HS256)
                .compact();
    }


    @Override
    public String generateAccessToken(User user) {
        return buildToken(user.getEmail(), ACCESS_TOKEN_EXPIRATION_TIME, "access");
    }

    @Override
    public String generateRefreshToken(User user){
        return buildToken(user.getEmail(), REFRESH_TOKEN_EXPIRATION_TIME, "refresh");
    }

    @Override
    public boolean isAccessToken(String token){
        return "access".equals(extractTokenType(token));
    }

    @Override
    public boolean isRefreshToken(String token) {
        return "refresh".equals(extractTokenType(token));
    }

    @Override
    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(jwtSigningKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractTokenType(String token) {
        return extractAllClaims(token).get("token_type", String.class);
    }

    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

}
