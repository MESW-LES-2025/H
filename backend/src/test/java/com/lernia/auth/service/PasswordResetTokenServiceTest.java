package com.lernia.auth.service;

import com.lernia.auth.entity.PasswordResetTokenEntity;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void createToken_deletesExistingTokensBeforeSaving() {
        UserEntity user = new UserEntity();
        when(tokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        tokenService.createToken(user);

        InOrder inOrder = inOrder(tokenRepository);
        inOrder.verify(tokenRepository).deleteByUser(user);
        inOrder.verify(tokenRepository).save(any());
    }

    @Test
    void validate_returnsEmptyIfTokenNotFound() {
        when(tokenRepository.findByTokenHash(any()))
                .thenReturn(Optional.empty());

        Optional<PasswordResetTokenEntity> result =
                tokenService.validate("does-not-exist");

        assertThat(result).isEmpty();
    }

    @Test
    void consume_marksTokenAsUsedAndSaves() {
        PasswordResetTokenEntity token = new PasswordResetTokenEntity();
        token.setUsedAt(null);

        tokenService.consume(token);

        assertThat(token.getUsedAt()).isNotNull();
        verify(tokenRepository).save(token);
    }

    @Test
    void hashToken_isDeterministic() {
        String raw = "same-token";

        String hash1 = tokenService.hashToken(raw);
        String hash2 = tokenService.hashToken(raw);

        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void createToken_generatesDifferentTokensEachTime() {
        UserEntity user = new UserEntity();
        when(tokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var t1 = tokenService.createToken(user).token();
        var t2 = tokenService.createToken(user).token();

        assertThat(t1).isNotEqualTo(t2);
    }

}