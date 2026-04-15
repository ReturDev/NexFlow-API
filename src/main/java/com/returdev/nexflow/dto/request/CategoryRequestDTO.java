package com.returdev.nexflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(title = "Category Request", description = "Schema for creating a new financial category.")
public record CategoryRequestDTO(
        @Size(max = 50, message = "{validation.max_size.message}")
        @NotBlank(message = "{validation.not_blank.message}")
        @Schema(example = "Leisure", description = "Name of the new category.", maxLength = 50)
        String name,

        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "/icons/leisure.svg", description = "Reference or path to the icon representing this category.")
        String iconResource
) {
}
