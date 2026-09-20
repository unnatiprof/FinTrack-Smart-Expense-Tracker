package com.unnati.fintrack.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unnati.fintrack.dto.request.ChangePasswordRequest;
import com.unnati.fintrack.dto.request.ProfileUpdateRequest;
import com.unnati.fintrack.dto.response.ProfileResponse;
import com.unnati.fintrack.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Validated
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                userService.getProfile(email));
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequest request) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                userService.updateProfile(email, request));
    }

    @PatchMapping("/profile/password")
    public ResponseEntity<String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        String email = authentication.getName();

        userService.changePassword(email, request);

        return ResponseEntity.ok(
                "Password changed successfully");
    }
}