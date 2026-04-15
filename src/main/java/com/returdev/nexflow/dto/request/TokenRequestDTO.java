package com.returdev.nexflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for token refresh requests.
 */
@Schema(title = "Token Refresh Request", description = "Request schema for obtaining a new access token using a refresh token.")
public record TokenRequestDTO(
        @NotBlank(message = "{validation.not_blank.message}")
        @Schema(description = "The valid refresh token provided during the last login or refresh.")
        String refreshToken
) {
}
