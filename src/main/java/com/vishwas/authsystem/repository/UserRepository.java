package com.vishwas.authsystem.repository;

// Create a JPA repository for User entity.


import com.vishwas.authsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    boolean existsByEmail(String email);
}
