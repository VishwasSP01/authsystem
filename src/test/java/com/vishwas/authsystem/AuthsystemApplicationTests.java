package com.vishwas.authsystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vishwas.authsystem.dto.LoginRequest;
import com.vishwas.authsystem.dto.RegisterRequest;
import com.vishwas.authsystem.entity.User;
import com.vishwas.authsystem.repository.UserRepository;
import com.vishwas.authsystem.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthsystemApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertNotNull(mockMvc);
    }

    @Test
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest("john_doe", "john@example.com", "securePassword123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        assertTrue(userRepository.existsByEmail("john@example.com"));
    }

    @Test
    void register_DuplicateEmail_Failure() throws Exception {
        // Pre-create user
        User user = User.builder()
                .username("existing_user")
                .email("duplicate@example.com")
                .password(passwordEncoder.encode("password"))
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        RegisterRequest request = new RegisterRequest("new_user", "duplicate@example.com", "password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void register_ValidationError_Failure() throws Exception {
        RegisterRequest request = new RegisterRequest("", "invalid-email", "");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.username").value("Username is required"))
                .andExpect(jsonPath("$.details.email").value("Invalid email format"))
                .andExpect(jsonPath("$.details.password").value("Password is required"));
    }

    @Test
    void login_Success() throws Exception {
        User user = User.builder()
                .username("test_user")
                .email("test@example.com")
                .password(passwordEncoder.encode("correct_password"))
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        LoginRequest request = new LoginRequest("test@example.com", "correct_password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void login_InvalidCredentials_Failure() throws Exception {
        User user = User.builder()
                .username("test_user")
                .email("test@example.com")
                .password(passwordEncoder.encode("correct_password"))
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        LoginRequest request = new LoginRequest("test@example.com", "wrong_password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void protectedApi_NoToken_Forbidden() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedApi_WithToken_Success() throws Exception {
        User user = User.builder()
                .username("user")
                .email("user@example.com")
                .password(passwordEncoder.encode("password"))
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        String token = jwtService.generateToken("user@example.com");

        mockMvc.perform(get("/api/test")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(status().isOk());
    }

    @Test
    void adminApi_UserRole_Forbidden() throws Exception {
        User user = User.builder()
                .username("regular_user")
                .email("user@example.com")
                .password(passwordEncoder.encode("password"))
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        String token = jwtService.generateToken("user@example.com");

        mockMvc.perform(get("/api/admin")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminApi_AdminRole_Success() throws Exception {
        User user = User.builder()
                .username("admin_user")
                .email("admin@example.com")
                .password(passwordEncoder.encode("password"))
                .role("ADMIN")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        String token = jwtService.generateToken("admin@example.com");

        mockMvc.perform(get("/api/admin")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Welcome Admin!"));
    }
}
