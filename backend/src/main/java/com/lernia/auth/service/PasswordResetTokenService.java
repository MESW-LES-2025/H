package com.lernia.auth.service;

import com.lernia.auth.entity.PasswordResetTokenEntity;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class PasswordResetTokenService {

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(15);

    private final PasswordResetTokenRepository tokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetTokenService(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public GeneratedToken createToken(UserEntity user) {
        tokenRepository.deleteByUser(user);

        String rawToken = generateRawToken();
        String hash = hashToken(rawToken);

        PasswordResetTokenEntity entity = new PasswordResetTokenEntity();
        entity.setUser(user);
        entity.setTokenHash(hash);
        entity.setCreatedAt(Instant.now());
        entity.setExpiresAt(Instant.now().plus(DEFAULT_TTL));

        tokenRepository.save(entity);
        return new GeneratedToken(rawToken, entity.getExpiresAt());
    }

    public Optional<PasswordResetTokenEntity> validate(String rawToken) {
        String hash = hashToken(rawToken);
        return tokenRepository.findByTokenHash(hash)
                .filter(token -> token.getUsedAt() == null)
                .filter(token -> token.getExpiresAt().isAfter(Instant.now()));
    }

    public void consume(PasswordResetTokenEntity token) {
        token.setUsedAt(Instant.now());
        tokenRepository.save(token);
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public record GeneratedToken(String token, Instant expiresAt) {}
}