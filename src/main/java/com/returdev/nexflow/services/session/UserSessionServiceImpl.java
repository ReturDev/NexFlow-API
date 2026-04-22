package com.returdev.nexflow.services.session;

import com.returdev.nexflow.dto.response.UserSessionResponseDTO;
import com.returdev.nexflow.mappers.UserSessionMapper;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.entities.UserSessionEntity;
import com.returdev.nexflow.model.enums.Role;
import com.returdev.nexflow.model.exceptions.AccessDeniedException;
import com.returdev.nexflow.model.exceptions.InvalidTokenException;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import com.returdev.nexflow.repositories.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of {@link UserSessionService} utilizing JPA repositories.
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository sessionRepository;
    private final UserSessionMapper mapper;

    /**
     * {@inheritDoc}
     *
     * @apiNote <b>Internal Use Only:</b> This method is intended for use by
     * authentication filters or providers. It does not perform authorization checks.
     */
    @Transactional
    @Override
    public void createSession(UserEntity user, String deviceInfo, String refreshToken) {
        UserSessionEntity sessionEntity = UserSessionEntity.builder()
                .deviceInfo(deviceInfo)
                .lastActive(LocalDateTime.now())
                .user(user)
                .refreshToken(refreshToken)
                .build();

        sessionRepository.save(sessionEntity);
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote <b>Administrative Use Only:</b> Access restricted to users with {@code Role.ADMIN}.
     */
    @Override
    public Page<UserSessionResponseDTO> getSessions(Pageable pageable) {
        return sessionRepository.findAll(pageable).map(mapper::toResponse);
    }

    /**
     * {@inheritDoc}
     *
     * @throws AccessDeniedException if the requester is neither the owner nor an admin.
     */
    @Override
    public Page<UserSessionResponseDTO> getSessionsByUserId(UUID userId, UserEntity requester, Pageable pageable) {
        verifyResourceAccess(userId, requester);
        return sessionRepository.findAllByUserId(userId, pageable).map(mapper::toResponse);
    }

    /**
     * {@inheritDoc}
     *
     * @throws InvalidTokenException if the token is null, expired, or does not exist.
     */
    @Override
    public UserSessionEntity getSessionByRefreshToken(String refreshToken) {
        return sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("exception.security.jwt.refresh_invalid"));
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote <b>Internal System Use:</b> This method is intended for use by
     * authentication filters or providers.
     */
    @Transactional
    @Override
    public void clearUserSessionsBeforeDate(LocalDateTime date, UUID userId) {
        sessionRepository.deleteByLastActiveBefore(date, userId);
    }

    /**
     * {@inheritDoc}
     *
     * @throws AccessDeniedException     if the requester does not own the session.
     * @throws ResourceNotFoundException if the session token is invalid.
     */
    @Override
    public void invalidateSessionByRefreshToken(String refreshToken, UserEntity requester) {
        UserSessionEntity session = getSessionByRefreshToken(refreshToken);
        verifyResourceAccess(session.getUser().getId(), requester);
        sessionRepository.deleteByRefreshToken(refreshToken);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the session ID does not exist.
     */
    @Transactional
    @Override
    public void invalidateSessionById(Long sessionId, UserEntity requester) {
        UserSessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("exception.session.not_found"));

        // Note: Consider adding ownership verification here if required by security policy
        sessionRepository.delete(session);
    }

    /**
     * {@inheritDoc}
     *
     * @throws AccessDeniedException if the requester is not authorized to clear the user's sessions.
     */
    @Transactional
    @Override
    public void invalidateAllSessions(UUID userId, UserEntity requester) {
        verifyResourceAccess(userId, requester);
        sessionRepository.deleteByUserId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSessionToken(UserSessionEntity session, String newRefreshToken) {
        session.setRefreshToken(newRefreshToken);
        session.setLastActive(LocalDateTime.now());
        sessionRepository.save(session);
    }

    /**
     * Validates that the requester is authorized to perform actions on a user's session data.
     *
     * @param sessionOwnerId the ID of the user who owns the session.
     * @param requester      the {@link UserEntity} attempting the action.
     * @throws AccessDeniedException if the requester lacks sufficient permissions.
     */
    private void verifyResourceAccess(UUID sessionOwnerId, UserEntity requester) {
        if (requester.getRole() != Role.ADMIN && !requester.getId().equals(sessionOwnerId)) {
            throw new AccessDeniedException("exception.security.authorization.denied");
        }
    }
}
