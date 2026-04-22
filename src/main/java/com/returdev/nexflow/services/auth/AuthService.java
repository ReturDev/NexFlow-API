package com.returdev.nexflow.services.auth;

import com.returdev.nexflow.dto.request.AuthRequestDTO;
import com.returdev.nexflow.dto.request.UserRequestDTO;
import com.returdev.nexflow.dto.response.AuthResponseDTO;
import com.returdev.nexflow.model.exceptions.InvalidTokenException;
import jakarta.validation.Valid;

/**
 * Service interface defining the contract for authentication and session management.
 */
public interface AuthService {

    /**
     * Authenticates a user and establishes a new session.
     *
     * @param authRequestDTO the user credentials (email and password).
     * @param deviceInfo     metadata about the client device (e.g., Browser, OS)
     *                       to track the session origin.
     * @return an {@link AuthResponseDTO} containing the generated access and refresh tokens.
     */
    AuthResponseDTO logIn(@Valid AuthRequestDTO authRequestDTO, String deviceInfo);

    /**
     * Registers a new user in the system and automatically signs them in.
     *
     * @param userRequestDTO the user's profile and credential data.
     * @param deviceInfo     the client device metadata for the initial session.
     * @return an {@link AuthResponseDTO} for the newly created account.
     */
    AuthResponseDTO register(@Valid UserRequestDTO userRequestDTO, String deviceInfo);

    /**
     * Invalidates a user's session by removing or blacklisting the provided refresh token.
     *
     * @param refreshToken The token to be revoked.
     */
    void logout(String refreshToken);

    /**
     * Issues a new pair of access and refresh tokens using a valid, non-expired refresh token.
     *
     * @param refreshToken the current valid refresh token.
     * @return a new {@link AuthResponseDTO} with fresh tokens.
     * @throws InvalidTokenException if the token is expired.
     */
    AuthResponseDTO refresh(String refreshToken);

}
