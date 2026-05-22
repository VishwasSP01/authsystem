package com.vishwas.authsystem.security;

// Create Spring Security configuration.

// This class configures Spring Security to allow unauthenticated access to the /api/auth/** endpoints and require
// authentication for all other endpoints. It also disables CSRF protection for simplicity.
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration

public class SecurityConfig { // Annotate with @Configuration to indicate that this class contains Spring configuration.

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}
