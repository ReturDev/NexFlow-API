package com.returdev.nexflow.services.auth;

import com.returdev.nexflow.dto.request.AuthRequestDTO;
import com.returdev.nexflow.dto.request.UserRequestDTO;
import com.returdev.nexflow.dto.response.AuthResponseDTO;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.entities.UserSessionEntity;
import com.returdev.nexflow.model.exceptions.InvalidTokenException;
import com.returdev.nexflow.repositories.UserSessionRepository;
import com.returdev.nexflow.services.jwt.JwtService;
import com.returdev.nexflow.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of the authentication service providing secure user access and session management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final UserSessionRepository sessionRepository;


    /** {@inheritDoc} */
    @Override
    public AuthResponseDTO logIn(AuthRequestDTO authRequestDTO, String deviceInfo) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.email(), authRequestDTO.password())
        );

        UserEntity user = (UserEntity) auth.getPrincipal();

        AuthResponseDTO tokens = generateTokens(user);

        createAndClearOldSessions(user, deviceInfo, tokens.refreshToken());

        return tokens;

    }

    /** {@inheritDoc} */
    @Override
    public AuthResponseDTO register(UserRequestDTO userRequestDTO, String deviceInfo) {

        UserEntity user = userService.saveUser(userRequestDTO);

        AuthResponseDTO tokens = generateTokens(user);

        generateSession(user, deviceInfo, tokens.refreshToken());

        return tokens;

    }

    /** {@inheritDoc} */
    @Override
    public void invalidateSession(String refreshToken) {
        sessionRepository.deleteByRefreshToken(refreshToken);
    }

    /** {@inheritDoc} */
    @Override
    public void invalidateAllSessions(String email) {
        sessionRepository.deleteByUserEmail(email);
    }

    /** {@inheritDoc} */
    @Override
    public AuthResponseDTO refresh(String refreshToken) {

        UserSessionEntity session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("exception.security.jwt.refresh_invalid"));

        if (!jwtService.isTokenValid(refreshToken, session.getUser())) {
            sessionRepository.delete(session);
            throw new InvalidTokenException("exception.security.jwt.refresh_invalid");
        }

        AuthResponseDTO newTokens = generateTokens(session.getUser());
        session.setRefreshToken(newTokens.refreshToken());
        session.setLastActive(LocalDateTime.now());

        sessionRepository.save(session);

        return newTokens;
    }

    /**
     * Internal helper to map a new session record into the database.
     */
    private void generateSession(UserEntity user, String deviceInfo, String refreshToken) {
        UserSessionEntity sessionEntity = UserSessionEntity.builder()
                .deviceInfo(deviceInfo)
                .lastActive(LocalDateTime.now())
                .user(user)
                .refreshToken(refreshToken)
                .build();

        sessionRepository.save(sessionEntity);

    }

    /**
     * Internal helper to generate a new Access and Refresh token pair for a user.
     */
    private AuthResponseDTO generateTokens(UserEntity user) {
        return new AuthResponseDTO(
                jwtService.generateToken(user),
                jwtService.generateRefreshToken(user)
        );
    }

    /**
     * Orchestrates the removal of stale user sessions and the creation of a new one.
     */
    private void createAndClearOldSessions(UserEntity user, String deviceInfo, String refreshToken) {

        LocalDateTime date = LocalDateTime.now().minusDays(JwtService.REFRESH_TOKEN_EXPIRATION_DAYS);

        sessionRepository.deleteByLastActiveBefore(date, user.getId());

        generateSession(user, deviceInfo, refreshToken);

    }


}
