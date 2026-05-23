package com.vishwas.authsystem.service;

// Create JWT service for token generation and validation.


// with this service, we can generate JWT tokens for authenticated users.
// The token will contain the user's email as the subject and will be signed using the HS256 algorithm with a secret key.
// The token will expire after 24 hours, ensuring that users need to re-authenticate after that period.
//Also make sure to add the io.jsonwebtoken library to your project dependencies to use this service.
// You can do this by adding the following dependency to your pom.xml if you're using Maven:


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;



@Service
public class JwtService {





    private static final String SECRET =
            "mySuperSecretKeyForJwtTokenGeneration123456789";

    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET.getBytes());



    public String generateToken(String email) {

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)
                )
                .signWith(key)
                .compact();
    }

    // Create JWT authentication filter.


    public boolean validateToken(String token) {

        try {

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

        public String extractEmail(String token) {

            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        }
}