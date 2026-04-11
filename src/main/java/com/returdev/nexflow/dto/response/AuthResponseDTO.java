package com.returdev.nexflow.dto.response;

/**
 * Data Transfer Object containing the security tokens issued upon successful authentication.
 *
 * @param token        the short-lived JWT (Access Token) used to authorize individual
 *                     API requests via the {@code Authorization: Bearer} header.
 * @param refreshToken the long-lived token used to obtain a new access token
 *                     once the current one expires.
 */
public record AuthResponseDTO(
        String token,
        String refreshToken
) {
}
