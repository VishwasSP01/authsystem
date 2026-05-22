package com.vishwas.authsystem.dto;

// Create DTO for authentication response.

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class AuthResponse {

    private String token;

    private String email;

    private String role;
}
