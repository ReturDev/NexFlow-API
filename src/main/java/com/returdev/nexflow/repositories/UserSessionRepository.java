package com.returdev.nexflow.repositories;

import com.returdev.nexflow.model.entities.UserSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing {@link UserSessionEntity} persistence.
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSessionEntity, Long> {

    /**
     * Locates a session associated with a specific refresh token.
     *
     * @param refreshToken the plain-text or hashed refresh token string.
     * @return an {@link Optional} containing the session if found, or empty if the token is invalid/expired.
     */
    Optional<UserSessionEntity> findByRefreshToken(String refreshToken);

    /**
     * Retrieves all active sessions for a given user, supporting pagination.
     *
     * @param userId   the unique identifier of the user.
     * @param pageable pagination and sorting information.
     * @return a {@link Page} of {@link UserSessionEntity} matching the user ID.
     */
    Page<UserSessionEntity> findAllByUserId(UUID userId, Pageable pageable);

    /**
     * Revokes all active sessions associated with a specific user id.
     *
     * @param userId the id of the user whose sessions should be invalidated.
     */
    void deleteByUserId(UUID userId);

    /**
     * Revokes a specific session identified by its refresh token.
     *
     * @param refreshToken the specific token to be removed from the system.
     */
    void deleteByRefreshToken(String refreshToken);

    /**
     * Cleans up expired or inactive sessions for a specific user.
     *
     * @param date   the cutoff timestamp; sessions with a {@code lastActive}
     *               time prior to this will be removed.
     * @param userId the unique identifier of the user whose sessions are being targeted.
     */
    @Modifying
    @Query("DELETE UserSessionEntity e where e.lastActive < :date AND e.user.id = :userId")
    void deleteByLastActiveBefore(LocalDateTime date, UUID userId);

}
