package com.vishwas.authsystem.service.impl;

import com.vishwas.authsystem.dto.AuthResponse;
import com.vishwas.authsystem.dto.LoginRequest;
import com.vishwas.authsystem.dto.RegisterRequest;
import com.vishwas.authsystem.entity.User;
import com.vishwas.authsystem.exception.UserAlreadyExistsException;
import com.vishwas.authsystem.repository.UserRepository;
import com.vishwas.authsystem.service.AuthService;
import com.vishwas.authsystem.service.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        // Encrypt password
        String encryptedPassword =
                passwordEncoder.encode(request.getPassword());

        // Create user
        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encryptedPassword);

        // Default role
        user.setRole("USER");

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Save user
        userRepository.save(user);

        // Generate JWT token
        String token = jwtService.generateToken(user.getEmail());

        // Return response
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Validate password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            throw new RuntimeException("Invalid password");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user.getEmail());

        // Return response
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole()
        );
    }
}