package com.example.inventorymanagementsystem.dtos.response.security;

import com.example.inventorymanagementsystem.helper.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data

@AllArgsConstructor
public class UserResponse {
   String username;
    private Long id;
    private String email;
    Role role;
}
