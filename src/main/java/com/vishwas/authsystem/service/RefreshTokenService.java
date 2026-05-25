package com.vishwas.authsystem.service;

import com.vishwas.authsystem.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(String email);

    RefreshToken verifyExpiration(RefreshToken token);

    Optional<RefreshToken> findByToken(String token);

    void revokeByToken(String token);

    void revokeByUser(String email);
}
