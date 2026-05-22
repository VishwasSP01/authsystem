package com.vishwas.authsystem.service;


import com.vishwas.authsystem.dto.AuthResponse;
import com.vishwas.authsystem.dto.RegisterRequest;


public interface AuthService {
    AuthResponse register(RegisterRequest request);
}
