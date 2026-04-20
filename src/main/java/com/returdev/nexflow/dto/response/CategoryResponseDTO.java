package com.returdev.nexflow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a transaction category.
 * <p>
 * This record is used to send category details to the client, typically for
 * populating selection menus or displaying category information in transaction lists.
 *
 * @param id           the unique database identifier for the category.
 * @param name         the display name of the category (e.g., "Groceries", "Salary").
 * @param iconResource a string reference or path to the icon associated with this category.
 * @param createdAt    the timestamp indicating when the category was created.
 * @param updatedAt    the timestamp indicating the last time the category was modified.
 */
@Schema(title = "Category Response", description = "Details of a financial category including audit timestamps.")
public record CategoryResponseDTO(
        @Schema(example = "1", description = "Unique identifier of the category.")
        Long id,
        @Schema(example = "Leisure", description = "Name of the category.")
        String name,
        @Schema(example = "/icons/leisure.svg", description = "Reference or URL path to the icon.")
        String iconResource,
        @Schema(example = "2026-04-15T10:00:00", description = "Timestamp when the category was initially created.")
        LocalDateTime createdAt,
        @Schema(example = "2026-04-15T12:30:00", description = "Timestamp of the last update to this category.")
        LocalDateTime updatedAt
) {
}