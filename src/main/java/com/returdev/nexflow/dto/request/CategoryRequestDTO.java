package com.returdev.nexflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating or updating a transaction category.
 * <p>
 * This record captures the essential display properties of a category and
 * enforces data integrity through Jakarta Validation constraints.
 *
 * @param name         the descriptive name of the category (e.g., "Dining Out").
 *                     Must not be blank and is limited to 50 characters.
 * @param iconResource the identifier or path for the category's visual icon.
 *                     Must not be empty to ensure every category has a visual marker.
 */
public record CategoryRequestDTO(

        @Size(max = 50, message = "{validation.max_size.message}")
        @NotBlank(message = "{validation.not_blank.message}")
        String name,
        @NotEmpty(message = "{validation.not_empty.message}")
        String iconResource
) {
}
