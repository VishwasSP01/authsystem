package com.vishwas.authsystem.controller;

import com.vishwas.authsystem.dto.AuthResponse;
import com.vishwas.authsystem.dto.LoginRequest;
import com.vishwas.authsystem.dto.RegisterRequest;
import com.vishwas.authsystem.dto.TokenRefreshRequest;
import com.vishwas.authsystem.dto.TokenRefreshResponse;
import com.vishwas.authsystem.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        System.out.println("REGISTER ENDPOINT HIT");
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody @Valid TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestBody @Valid TokenRefreshRequest request) {
        authService.logout(request);
        return ResponseEntity.ok("User logged out successfully");
    }
}
