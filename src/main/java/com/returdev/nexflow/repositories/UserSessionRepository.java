package com.returdev.nexflow.repositories;

import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.entities.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing {@link UserSessionEntity} persistence.
 */
public interface UserSessionRepository extends JpaRepository<UserSessionEntity, Long> {

    /**
     * Locates a session associated with a specific refresh token.
     *
     * @param refreshToken the plain-text or hashed refresh token string.
     * @return an {@link Optional} containing the session if found, or empty if the token is invalid/expired.
     */
    Optional<UserSessionEntity> findByRefreshToken(String refreshToken);

    /**
     * Revokes all active sessions for a specific user.
     *
     * @param user the {@link UserEntity} whose sessions should be invalidated.
     */
    void deleteByUser(UserEntity user);

    /**
     * Revokes a specific session identified by its refresh token.
     *
     * @param refreshToken the specific token to be removed from the system.
     */
    void deleteByRefreshToken(String refreshToken);

}
