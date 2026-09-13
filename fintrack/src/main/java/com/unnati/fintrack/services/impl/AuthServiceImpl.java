package com.unnati.fintrack.services.impl;

import org.springframework.stereotype.Service;

import com.unnati.fintrack.dto.request.LoginRequest;
import com.unnati.fintrack.dto.request.RegisterRequest;
import com.unnati.fintrack.dto.response.LoginResponse;
import com.unnati.fintrack.entity.User;
import com.unnati.fintrack.services.AuthService;
import com.unnati.fintrack.services.UserService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    public AuthServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public LoginResponse register(RegisterRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User savedUser = userService.save(user);

        return new LoginResponse(
                null,
                "Bearer",
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return new LoginResponse(
                null,
                "Bearer",
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}