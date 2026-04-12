package com.returdev.nexflow.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for token refresh requests.
 */
public record TokenRequestDTO(
        @NotBlank(message = "{validation.not_blank.message}") String refreshToken
) {
}
