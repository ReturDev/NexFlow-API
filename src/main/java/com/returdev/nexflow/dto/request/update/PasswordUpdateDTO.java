package com.returdev.nexflow.dto.request.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for password update requests.
 *
 * @param oldPassword the user's current password, used for identity verification.
 * @param newPassword the desired new password, which must be at least 8 characters long.
 */
@Schema(title = "Password Update", description = "Schema for changing the user's account password.")
public record PasswordUpdateDTO(
        @Schema(
                description = "The current password required for verification.",
                format = "password"
        )
        @NotBlank(message = "{validation.not_blank.message}")
        String oldPassword,

        @Schema(
                description = "The new password to set. Must be secure and hard to guess.",
                format = "password",
                minLength = 8
        )
        @NotBlank(message = "{validation.not_blank.message}")
        @Size(min = 8, message = "{validation.password.too_short}")
        String newPassword
) {
}
