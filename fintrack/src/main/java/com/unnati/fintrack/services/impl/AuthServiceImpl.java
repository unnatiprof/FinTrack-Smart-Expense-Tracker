package com.unnati.fintrack.services.impl;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.unnati.fintrack.dto.request.LoginRequest;
import com.unnati.fintrack.dto.request.RegisterRequest;
import com.unnati.fintrack.dto.response.LoginResponse;
import com.unnati.fintrack.entity.User;
import com.unnati.fintrack.events.UserLoggedInEvent;
import com.unnati.fintrack.security.JwtService;
import com.unnati.fintrack.services.AuthService;
import com.unnati.fintrack.services.UserService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApplicationEventPublisher eventPublisher;

    public AuthServiceImpl(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            ApplicationEventPublisher eventPublisher) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public LoginResponse register(RegisterRequest request) {

        if (!request.getPassword()
                .equals(request.getConfirmPassword())) {

            throw new RuntimeException("Passwords do not match");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Encrypt password before saving
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        User savedUser = userService.save(user);

        String token = jwtService.generateToken(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );

        return new LoginResponse(
                token,
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
                        new RuntimeException(
                                "Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid email or password");
        }

        // Update last successful login time
        user.setLastLogin(LocalDateTime.now());

        userService.update(user.getId(), user);

        // Publish login event for AuditLog
        eventPublisher.publishEvent(
                new UserLoggedInEvent(
                        user.getId(),
                        user.getName(),
                        user.getEmail()));

        // Generate JWT
        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        return new LoginResponse(
                token,
                "Bearer",
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}