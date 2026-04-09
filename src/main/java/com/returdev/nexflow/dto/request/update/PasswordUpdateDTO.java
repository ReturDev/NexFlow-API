package com.returdev.nexflow.dto.request.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for password update requests.
 *
 * @param oldPassword the user's current password, used for identity verification.
 * @param newPassword the desired new password, which must be at least 8 characters long.
 */
public record PasswordUpdateDTO(
        @NotBlank(message = "{validation.not_blank.message}")
        String oldPassword,
        @NotBlank(message = "{validation.not_blank.message}")
        @Size(min = 8, message = "{validation.password.too_short}")
        String newPassword
) {
}
