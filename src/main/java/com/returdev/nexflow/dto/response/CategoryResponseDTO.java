package com.returdev.nexflow.dto.response;

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
public record CategoryResponseDTO(
        Long id,
        String name,
        String iconResource,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}