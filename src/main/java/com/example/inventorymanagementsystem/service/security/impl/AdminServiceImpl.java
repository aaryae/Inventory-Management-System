package com.example.inventorymanagementsystem.service.security.impl;

import com.example.inventorymanagementsystem.dtos.response.security.UserResponse;
import com.example.inventorymanagementsystem.exception.DataNotFoundException;
import com.example.inventorymanagementsystem.model.User;
import com.example.inventorymanagementsystem.repository.security.UserRepository;
import com.example.inventorymanagementsystem.service.security.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
        private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found with id " + id));
        return new UserResponse(user.getUsername(), user.getId(), user.getEmail(), user.getRole());
    }

    @Override
    public void updateUserById(Long id, UserResponse userResponse) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found with id " + id));

        user.setEmail(userResponse.getEmail());
        userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found with id " + id));
        userRepository.deleteById(id);
    }



}

