package com.vishwas.authsystem.controller;

// Create REST controller for authentication.
// Requirements:
// - Annotate with @RestController
// - Base path should be /api/auth
// - Inject AuthService using constructor injection
// - Create POST endpoint for /register
// - Endpoint should accept RegisterRequest
// - Endpoint should return AuthResponse


import com.vishwas.authsystem.dto.AuthResponse;
import com.vishwas.authsystem.dto.RegisterRequest;
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
}
