package com.returdev.nexflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for capturing user registration or update requests.
 * <p>
 * Includes Jakarta Validation constraints to ensure data integrity before
 * it reaches the service layer.
 *
 * @param name     the first name of the user (max 50 characters).
 * @param surnames the last names of the user (max 100 characters).
 * @param email    the unique contact email (must be a valid email format).
 * @param password the user's plain-text password (minimum 8 characters).
 */
public record UserRequestDTO(
        @NotBlank(message = "{validation.not_blank.message}")
        @Size(max = 50, message = "{validation.max_size.message}")
        String name,

        @NotNull(message = "{validation.not_null.message}")
        @Size(max = 100, message = "{validation.max_size.message}")
        String surnames,

        @Email(message = "{validation.email.invalid}")
        @NotBlank(message = "{validation.not_blank.message}")
        @Size(max = 100, message = "{validation.max_size.message}")
        String email,

        @NotBlank(message = "{validation.not_blank.message}")
        @Size(min = 8, message = "{validation.password.too_short}")
        String password
) {
}
