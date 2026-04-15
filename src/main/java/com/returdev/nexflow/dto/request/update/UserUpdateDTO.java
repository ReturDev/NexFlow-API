package com.returdev.nexflow.dto.request.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating User profile information.
 *
 * @param name     updated first name (cannot be empty).
 * @param surnames updated last names.
 */
@Schema(title = "User Update", description = "Schema for updating the user's profile information.")
public record UserUpdateDTO(
        @Size(min = 1, max = 50, message = "{validation.size.message}")
        @Schema(
                example = "John",
                description = "The first name of the user.",
                minLength = 1,
                maxLength = 50
        )
        String name,

        @Size(max = 100, message = "{validation.max_size.message}")
        @Schema(
                example = "Doe Smith",
                description = "The user's surnames or family names.",
                maxLength = 100
        )
        String surnames
) {
}
