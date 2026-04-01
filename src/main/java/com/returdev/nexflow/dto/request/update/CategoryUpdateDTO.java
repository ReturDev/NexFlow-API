package com.returdev.nexflow.dto.request.update;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing Category.
 *
 * @param name         updated display name for the category (max 50 chars).
 * @param iconResource updated reference to the category's visual icon.
 */
public record CategoryUpdateDTO(
        @Size(min = 3, max = 50, message = "{validation.size.message}")
        String name,
        String iconResource
) {
}
