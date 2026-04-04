package com.returdev.nexflow.dto.request.update;

import jakarta.validation.constraints.*;

/**
 * DTO for updating User profile information.
 *
 * @param name     updated first name (cannot be empty).
 * @param surnames updated last names.
 */
public record UserUpdateDTO(
        @Size(min = 1, max = 50, message = "{validation.size.message}")
        String name,
        @Size(max = 100, message = "{validation.max_size.message}")
        String surnames
) {
}
