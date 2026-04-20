package com.returdev.nexflow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object containing the security tokens issued upon successful authentication.
 *
 * @param token        the short-lived JWT (Access Token) used to authorize individual
 *                     API requests via the {@code Authorization: Bearer} header.
 * @param refreshToken the long-lived token used to obtain a new access token
 *                     once the current one expires.
 */
@Schema(title = "Authentication Success Response", description = "Contains the tokens required for authorized requests.")
public record AuthResponseDTO(
        @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "JWT Access Token for authenticating secured endpoints.")
        String token,
        @Schema(example = "d8e8f8a8-b8c8-4d8e-a8f8-b8c8d8e8f8a8", description = "Token used to obtain a new access token when the current one expires.")
        String refreshToken
) {
}
