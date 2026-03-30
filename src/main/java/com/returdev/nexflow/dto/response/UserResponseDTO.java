package com.returdev.nexflow.dto.response;

import com.returdev.nexflow.model.enums.Role;

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
public record UserResponseDTO(
        UUID id,
        String name,
        String surnames,
        Role role,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}