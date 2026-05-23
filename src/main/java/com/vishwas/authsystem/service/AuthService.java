package com.vishwas.authsystem.service;


import com.vishwas.authsystem.dto.AuthResponse;
import com.vishwas.authsystem.dto.RegisterRequest;
import com.vishwas.authsystem.dto.LoginRequest;


public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
