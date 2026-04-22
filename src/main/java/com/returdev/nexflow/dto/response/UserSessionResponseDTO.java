package com.returdev.nexflow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing an active user session.
 *
 * @param id         the unique identifier for the session.
 * @param deviceInfo details about the client device (e.g., model, browser, or OS version).
 * @param lastActive the timestamp indicating the last recorded activity for this session.
 * @param userId     the unique identifier of the user who owns this session.
 */
@Schema(name = "User Session Response", description = "Data transfer object representing an active user session")
public record UserSessionResponseDTO(

        @Schema(description = "Unique identifier of the session", example = "1024")
        Long id,

        @Schema(description = "Information about the browser and operating system",
                example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/123.0.0.0")
        String deviceInfo,

        @Schema(description = "Timestamp of the last time the session was used",
                example = "2026-04-21T17:10:32")
        LocalDateTime lastActive,

        @Schema(description = "The unique UUID of the user who owns this session",
                example = "550e8400-e29b-41d4-a716-446655440000")
        UUID userId
) {
}
