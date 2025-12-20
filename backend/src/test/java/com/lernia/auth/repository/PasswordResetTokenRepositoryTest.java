package com.lernia.auth.repository;

import com.lernia.auth.entity.PasswordResetTokenEntity;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.entity.enums.Gender;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PasswordResetTokenRepositoryTest {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EntityManager em;

    @Test
    void findByTokenHash_returnsTokenWhenExists() {
        UserEntity user = buildUser();
        em.persist(user);

        PasswordResetTokenEntity token = new PasswordResetTokenEntity();
        token.setUser(user);
        token.setTokenHash("hash123");
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plusSeconds(900));

        em.persist(token);
        em.flush();
        em.clear();

        Optional<PasswordResetTokenEntity> result =
                tokenRepository.findByTokenHash("hash123");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getId())
                .isEqualTo(user.getId());
    }

    @Test
    void findByTokenHash_returnsEmptyWhenNotFound() {
        Optional<PasswordResetTokenEntity> result =
                tokenRepository.findByTokenHash("does-not-exist");

        assertThat(result).isEmpty();
    }

    @Test
    void deleteByUser_deletesAllTokensForUser() {
        UserEntity user = buildUser();
        em.persist(user);

        PasswordResetTokenEntity t1 = new PasswordResetTokenEntity();
        t1.setUser(user);
        t1.setTokenHash("hash1");
        t1.setCreatedAt(Instant.now());
        t1.setExpiresAt(Instant.now().plusSeconds(900));

        PasswordResetTokenEntity t2 = new PasswordResetTokenEntity();
        t2.setUser(user);
        t2.setTokenHash("hash2");
        t2.setCreatedAt(Instant.now());
        t2.setExpiresAt(Instant.now().plusSeconds(900));

        em.persist(t1);
        em.persist(t2);
        em.flush();

        tokenRepository.deleteByUser(user);
        em.flush();
        em.clear();

        List<PasswordResetTokenEntity> remaining =
                em.createQuery(
                                "select t from PasswordResetTokenEntity t where t.user = :user",
                                PasswordResetTokenEntity.class)
                        .setParameter("user", user)
                        .getResultList();

        assertThat(remaining).isEmpty();
    }

    @Test
    void deleteByUser_doesNotAffectOtherUsersTokens() {
        UserEntity user1 = buildUser();
        UserEntity user2 = buildUser();
        em.persist(user1);
        em.persist(user2);

        PasswordResetTokenEntity t1 = new PasswordResetTokenEntity();
        t1.setUser(user1);
        t1.setTokenHash("hash1");
        t1.setCreatedAt(Instant.now());
        t1.setExpiresAt(Instant.now().plusSeconds(900));

        PasswordResetTokenEntity t2 = new PasswordResetTokenEntity();
        t2.setUser(user2);
        t2.setTokenHash("hash2");
        t2.setCreatedAt(Instant.now());
        t2.setExpiresAt(Instant.now().plusSeconds(900));

        em.persist(t1);
        em.persist(t2);
        em.flush();

        tokenRepository.deleteByUser(user1);
        em.flush();
        em.clear();

        assertThat(tokenRepository.findByTokenHash("hash1")).isEmpty();
        assertThat(tokenRepository.findByTokenHash("hash2")).isPresent();
    }


    private UserEntity buildUser() {
        UserEntity user = new UserEntity();
        user.setGender(Gender.FEMALE);
        return user;
    }

}
