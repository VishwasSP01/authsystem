package com.vishwas.authsystem.entity;

// Create an industry-standard JPA entity for authentication system users.
// Requirements:
// - Use Lombok annotations
// - Use JPA annotations
// - Include fields:
//   id
//   username
//   email
//   password
//   role
//   createdAt
//   updatedAt
// - email should be unique
// - use GenerationType.IDENTITY for primary key

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
