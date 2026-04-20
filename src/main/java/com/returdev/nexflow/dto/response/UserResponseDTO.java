package com.returdev.nexflow.dto.response;

import com.returdev.nexflow.model.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing a user's profile and audit information.
 * <p>
 * This record is used to provide a complete view of a user's account details,
 * including lifecycle timestamps, typically for administrative or profile-view
 * responses.
 *
 * @param id        the unique identifier of the user.
 * @param name      the user's first name.
 * @param surnames  the user's last names.
 * @param role      the {@link Role} assigned to the user, defining their access levels.
 * @param email     the user's registered email address.
 * @param createdAt the timestamp indicating when the user account was first created.
 * @param updatedAt the timestamp indicating the last time the user's information was modified.
 */
@Schema(title = "User Response", description = "Public profile and account information of a user.")
public record UserResponseDTO(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000", description = "The unique UUID of the user.")
        UUID id,

        @Schema(example = "John", description = "First name.")
        String name,

        @Schema(example = "Doe Smith", description = "Surnames.")
        String surnames,

        @Schema(example = "USER", implementation = Role.class, description = "The security role assigned to the user.")
        Role role,

        @Schema(example = "john.doe@example.com", description = "Registered email address.")
        String email,

        @Schema(example = "2026-01-01T00:00:00")
        LocalDateTime createdAt,

        @Schema(example = "2026-04-15T10:00:00")
        LocalDateTime updatedAt
) {
}