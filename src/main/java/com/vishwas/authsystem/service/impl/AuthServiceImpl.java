package com.vishwas.authsystem.service.impl;

// Create implementation for AuthService.
// Requirements:
// - Implement AuthService
// - Annotate with @Service
// - Inject UserRepository
// - Use constructor injection

import com.vishwas.authsystem.dto.AuthResponse;
import com.vishwas.authsystem.dto.LoginRequest;
import com.vishwas.authsystem.dto.RegisterRequest;
import com.vishwas.authsystem.entity.User;
import com.vishwas.authsystem.repository.UserRepository;
import com.vishwas.authsystem.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.vishwas.authsystem.exception.UserAlreadyExistsException;
import com.vishwas.authsystem.service.JwtService;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, PasswordEncoder passwordEncoder1, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder1;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Implementation for registration logic

        // Implement user registration logic.

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("Email already exists");
            }

            // Encrypt password
            String encryptedPassword = passwordEncoder.encode(request.getPassword());

            // Create User entity
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(encryptedPassword);
            user.setRole("USER");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            // Save user
            userRepository.save(user);

            // Return AuthResponse
            return new AuthResponse(jwtService.generateToken(user.getEmail()), user.getEmail(), user.getRole());


    }

    @Override
    // Implement login logic.

    // Here I will implement the login logic. I will check if the user exists and if the password matches.
    // If both are correct, I will return an AuthResponse with a dummy token.

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return new AuthResponse(jwtService.generateToken(user.getEmail()), user.getEmail(), user.getRole());



    }

}
