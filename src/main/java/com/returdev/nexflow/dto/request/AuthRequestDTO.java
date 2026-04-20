package com.returdev.nexflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object representing an authentication request.
 *
 * @param email    the unique email address associated with the user account.
 *                 Must follow a valid email pattern (e.g., user@example.com).
 * @param password the plain-text password provided for identity verification.
 *                 Cannot be null or empty.
 */
@Schema(title = "Authentication Request", description = "Credentials required to authenticate and obtain access tokens.")
public record AuthRequestDTO(
        @Email(message = "{validation.email.invalid}")
        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "user@example.com", description = "User's registered email address.")
        String email,

        @NotBlank(message = "{validation.not_blank.message}")
        @Schema(description = "User's password.", format = "password")
        String password
) {
}
