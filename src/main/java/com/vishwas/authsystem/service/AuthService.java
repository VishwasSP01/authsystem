package com.vishwas.authsystem.service;

import com.vishwas.authsystem.dto.AuthResponse;
import com.vishwas.authsystem.dto.LoginRequest;
import com.vishwas.authsystem.dto.RegisterRequest;
import com.vishwas.authsystem.dto.TokenRefreshRequest;
import com.vishwas.authsystem.dto.TokenRefreshResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    TokenRefreshResponse refresh(TokenRefreshRequest request);
    void logout(TokenRefreshRequest request);
}
