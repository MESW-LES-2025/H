package com.lernia.auth.repository;

import com.lernia.auth.entity.PasswordResetTokenEntity;
import com.lernia.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
    Optional<PasswordResetTokenEntity> findByTokenHash(String tokenHash);
    void deleteByUser(UserEntity user);
}