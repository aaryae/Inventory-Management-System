package com.example.inventorymanagementsystem.service.security.impl;

import com.example.inventorymanagementsystem.dtos.request.PasswordResetRequest;
import com.example.inventorymanagementsystem.dtos.request.security.LoginRequest;
import com.example.inventorymanagementsystem.dtos.request.security.RefreshTokenRequest;
import com.example.inventorymanagementsystem.dtos.request.security.RegisterRequest;
import com.example.inventorymanagementsystem.exception.DataNotFoundException;
import com.example.inventorymanagementsystem.exception.DuplicateResourceException;
import com.example.inventorymanagementsystem.exception.ResourceNotFoundExceptionHandler;
import com.example.inventorymanagementsystem.exception.ValidationException;
import com.example.inventorymanagementsystem.helper.Role;
import com.example.inventorymanagementsystem.model.User;
import com.example.inventorymanagementsystem.repository.security.UserRepository;
import com.example.inventorymanagementsystem.service.MailService;
import com.example.inventorymanagementsystem.service.security.AuthService;
import com.example.inventorymanagementsystem.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;

    @Override
    public void register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.email());
        if (existingUser.isPresent()) {
            throw new DuplicateResourceException("User already exists with username " + request.email());
        }
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .passwordLastUpdated(LocalDateTime.now())
                .role(Role.USER)
                .build();
        userRepository.save(user);
        mailService.sendWelcomeMail(user);
    }


    public Map<String, String> login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResourceNotFoundExceptionHandler("User", "username", loginRequest.email()));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new ResourceNotFoundExceptionHandler("User", "credentials", "Invalid username or password");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime passwordLastUpdated = user.getPasswordLastUpdated();

        if (passwordLastUpdated.isBefore(now.minusYears(1))) {
            mailService.sendPasswordReset(user);
            throw new DataNotFoundException("Your password has expired. A reset link has been sent to your email.");
        }

        if (passwordLastUpdated.isBefore(now.minusMonths(11))) {
            mailService.sendPasswordAboutToExpire(user);
        }

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("refreshToken", refreshToken);
        response.put("username",user.getUsername());
        return response;
    }

    @Override
    public void sendResetCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundExceptionHandler("User", "email", email));

        mailService.sendPasswordReset(user);
    }

    @Override
    public void verifyAndResetPassword(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundExceptionHandler("User", "email", request.email()));

        boolean valid = mailService.verify(request.code(), user);

        if (!valid) {
            throw new ValidationException("Invalid or expired code.");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    public Map<String, String> refreshToken(RefreshTokenRequest request) {
        String email = jwtService.validateToken(request.accessToken());

        if (email.startsWith("error:")) {
            throw new ValidationException("Invalid refresh token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundExceptionHandler("User", "email", email));

        String newAccessToken = jwtService.generateRefreshToken(user);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        return tokens;
    }


}





