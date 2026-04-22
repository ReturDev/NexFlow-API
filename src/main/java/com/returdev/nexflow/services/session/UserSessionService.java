package com.returdev.nexflow.services.session;

import com.returdev.nexflow.dto.response.UserSessionResponseDTO;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.entities.UserSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service interface for managing user authentication sessions.
 */
public interface UserSessionService {

    /**
     * Registers a new session for a user.
     *
     * @param user         the {@link UserEntity} to whom the session belongs.
     * @param deviceInfo   details describing the client device.
     * @param refreshToken the token used for refreshing access credentials.
     */
    void createSession(UserEntity user, String deviceInfo, String refreshToken);

    /**
     * Retrieves a paginated list of all active sessions in the system.
     *
     * @param pageable pagination and sorting configuration.
     * @return a {@link Page} of active {@link UserSessionResponseDTO}.
     */
    Page<UserSessionResponseDTO> getSessions(Pageable pageable);

    /**
     * Retrieves a paginated list of active sessions for a specific user.
     *
     * @param userId    the unique ID of the user.
     * @param requester the {@link UserEntity} requesting the data (used for authorization).
     * @param pageable  pagination and sorting configuration.
     * @return a {@link Page} of active {@link UserSessionResponseDTO}.
     */
    Page<UserSessionResponseDTO> getSessionsByUserId(UUID userId, UserEntity requester, Pageable pageable);

    /**
     * Finds an active session based on its refresh token.
     *
     * @param refreshToken the token string.
     * @return the {@link UserSessionEntity} if found.
     */
    UserSessionEntity getSessionByRefreshToken(String refreshToken);

    /**
     * Removes all sessions for a user that were last active before the specified date.
     *
     * @param date   the cutoff timestamp.
     * @param userId the unique ID of the user.
     */
    void clearUserSessionsBeforeDate(LocalDateTime date, UUID userId);

    /**
     * Invalidates a specific session by its identifier.
     *
     * @param sessionId the unique ID of the session.
     * @param requester the {@link UserEntity} requesting the invalidation.
     */
    void invalidateSessionById(Long sessionId, UserEntity requester);

    /**
     * Invalidates a specific session identified by its refresh token.
     *
     * @param refreshToken the token string.
     * @param requester    the {@link UserEntity} requesting the invalidation.
     */
    void invalidateSessionByRefreshToken(String refreshToken, UserEntity requester);

    /**
     * Invalidates all active sessions belonging to a specific user.
     *
     * @param userId    the unique ID of the user.
     * @param requester the {@link UserEntity} requesting the invalidation.
     */
    void invalidateAllSessions(UUID userId, UserEntity requester);

    /**
     * Rotates the refresh token for an existing session.
     *
     * @param session         the {@link UserSessionEntity} to update.
     * @param newRefreshToken the new token string to be stored.
     */
    void updateSessionToken(UserSessionEntity session, String newRefreshToken);

}
