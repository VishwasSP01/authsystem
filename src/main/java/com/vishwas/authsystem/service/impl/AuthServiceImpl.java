package com.vishwas.authsystem.service.impl;

import com.vishwas.authsystem.dto.AuthResponse;
import com.vishwas.authsystem.dto.LoginRequest;
import com.vishwas.authsystem.dto.RegisterRequest;
import com.vishwas.authsystem.dto.TokenRefreshRequest;
import com.vishwas.authsystem.dto.TokenRefreshResponse;
import com.vishwas.authsystem.entity.RefreshToken;
import com.vishwas.authsystem.entity.User;
import com.vishwas.authsystem.exception.UserAlreadyExistsException;
import com.vishwas.authsystem.exception.InvalidCredentialsException;
import com.vishwas.authsystem.exception.TokenRefreshException;
import com.vishwas.authsystem.repository.UserRepository;
import com.vishwas.authsystem.service.AuthService;
import com.vishwas.authsystem.service.JwtService;
import com.vishwas.authsystem.service.RefreshTokenService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        // Encrypt password
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encryptedPassword);
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Save user
        userRepository.save(user);

        // Generate tokens
        String token = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        // Return response
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Generate tokens
        String token = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        // Return response
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateToken(user.getEmail());
                    // Rotate the refresh token (RTR Pattern)
                    RefreshToken rotatedRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());
                    return new TokenRefreshResponse(accessToken, rotatedRefreshToken.getToken());
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }

    @Override
    public void logout(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        refreshTokenService.revokeByToken(requestRefreshToken);
    }
}