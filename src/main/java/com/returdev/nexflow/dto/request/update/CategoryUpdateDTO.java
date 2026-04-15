package com.returdev.nexflow.dto.request.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing Category.
 *
 * @param name         updated display name for the category (max 50 chars).
 * @param iconResource updated reference to the category's visual icon.
 */
@Schema(title = "Category Update", description = "Schema for updating an existing financial category.")
public record CategoryUpdateDTO(
        @Size(min = 3, max = 50, message = "{validation.size.message}")
        @Schema(
                example = "Fuel",
                description = "The name of the category.",
                minLength = 3,
                maxLength = 50
        )
        String name,

        @Schema(
                example = "/resources/icons/categories/fuel-icon.svg",
                description = "Path or URL to the icon resource representing the category."
        )
        String iconResource
) {
}
