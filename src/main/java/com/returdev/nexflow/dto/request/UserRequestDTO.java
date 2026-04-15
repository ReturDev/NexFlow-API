package com.returdev.nexflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(title = "User Registration Request", description = "Personal information and credentials required to register a new user.")
public record UserRequestDTO(
        @NotBlank(message = "{validation.not_blank.message}")
        @Size(max = 50, message = "{validation.max_size.message}")
        @Schema(example = "John", description = "User's first name.", maxLength = 50)
        String name,

        @NotNull(message = "{validation.not_null.message}")
        @Size(max = 100, message = "{validation.max_size.message}")
        @Schema(example = "Doe Smith", description = "User's full surnames.", maxLength = 100)
        String surnames,

        @Email(message = "{validation.email.invalid}")
        @NotBlank(message = "{validation.not_blank.message}")
        @Size(max = 100, message = "{validation.max_size.message}")
        @Schema(example = "john.doe@example.com", description = "Unique email address for account access and notifications.", maxLength = 100)
        String email,

        @NotBlank(message = "{validation.not_blank.message}")
        @Size(min = 8, message = "{validation.password.too_short}")
        @Schema(description = "Account password. Must be at least 8 characters long.", format = "password", minLength = 8)
        String password
) {
}
