package com.returdev.nexflow.dto.request;

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
public record AuthRequestDTO(
        @Email @NotNull(message = "{validation.not_null.message}") String email,
        @NotBlank(message = "{validation.not_blank.message}") String password
) {}
