package com.unnati.fintrack.services;

import com.unnati.fintrack.dto.request.LoginRequest;
import com.unnati.fintrack.dto.request.RegisterRequest;
import com.unnati.fintrack.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}