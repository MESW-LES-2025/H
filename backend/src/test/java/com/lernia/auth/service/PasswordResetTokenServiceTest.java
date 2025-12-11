package com.lernia.auth.service;

import com.lernia.auth.entity.PasswordResetTokenEntity;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class PasswordResetTokenServiceTest {

    private PasswordResetTokenRepository tokenRepository;
    private PasswordResetTokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenRepository = mock(PasswordResetTokenRepository.class);
        tokenService = new PasswordResetTokenService(tokenRepository);
    }

    @Test
    void createToken_savesTokenAndReturnsRawToken() {
        UserEntity user = new UserEntity();
        when(tokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PasswordResetTokenService.GeneratedToken generated = tokenService.createToken(user);

        assertThat(generated.token()).isNotEmpty();
        assertThat(generated.expiresAt()).isAfter(Instant.now());
        verify(tokenRepository).deleteByUser(user);
        verify(tokenRepository).save(any(PasswordResetTokenEntity.class));
    }

    @Test
    void validate_returnsTokenIfValid() {
        String rawToken = "rawtoken";
        String hash = tokenService.hashToken(rawToken);

        PasswordResetTokenEntity entity = new PasswordResetTokenEntity();
        entity.setTokenHash(hash);
        entity.setExpiresAt(Instant.now().plusSeconds(60));
        entity.setUsedAt(null);

        when(tokenRepository.findByTokenHash(hash)).thenReturn(Optional.of(entity));

        Optional<PasswordResetTokenEntity> result = tokenService.validate(rawToken);
        assertThat(result).isPresent();
    }

    @Test
    void validate_returnsEmptyIfExpiredOrUsed() {
        String rawToken = "rawtoken";
        String hash = tokenService.hashToken(rawToken);

        PasswordResetTokenEntity expired = new PasswordResetTokenEntity();
        expired.setTokenHash(hash);
        expired.setExpiresAt(Instant.now().minusSeconds(60));
        expired.setUsedAt(null);

        PasswordResetTokenEntity used = new PasswordResetTokenEntity();
        used.setTokenHash(hash);
        used.setExpiresAt(Instant.now().plusSeconds(60));
        used.setUsedAt(Instant.now());

        when(tokenRepository.findByTokenHash(hash)).thenReturn(Optional.of(expired));
        assertThat(tokenService.validate(rawToken)).isEmpty();

        when(tokenRepository.findByTokenHash(hash)).thenReturn(Optional.of(used));
        assertThat(tokenService.validate(rawToken)).isEmpty();
    }
}