package com.datingapp.datingapp.services;

import com.datingapp.datingapp.entity.RefreshToken;
import com.datingapp.datingapp.repository.RefreshTokenRepo;
import com.datingapp.datingapp.repository.UserRepo;
import com.datingapp.datingapp.entity.User;
import com.datingapp.datingapp.exception.TokenRefreshException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration-ms}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;

    public RefreshTokenService(RefreshTokenRepo refreshTokenRepo,
                               UserRepo userRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
        this.userRepo = userRepo;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new TokenRefreshException("User not found"));

        // Optionally delete existing tokens:
        refreshTokenRepo.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        return refreshTokenRepo.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepo.delete(token);
            throw new TokenRefreshException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new TokenRefreshException("User not found"));
        return refreshTokenRepo.deleteByUser(user);
    }

    @Transactional
    public boolean existsByUserId(Integer userId) {
        return refreshTokenRepo.existsByUser_PkUser(userId);
    }
    @Transactional
    public RefreshToken getTokenByUserId(Integer userId) {
        Optional<RefreshToken> token = refreshTokenRepo.findRefreshTokensByUser_PkUser(userId);
        if (token.isPresent()) {
            return token.get();
        }
        return null;
    }
}
