package com.returdev.nexflow.controllers.session;

import com.returdev.nexflow.annotations.swagger.*;
import com.returdev.nexflow.dto.response.UserSessionResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.PaginationWrapperResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "User Sessions", description = "Endpoints for managing and monitoring user active sessions")
@SecurityRequirement(name = "Bearer Authentication")
@UnauthorizedResponseCode
@ForbiddenResponseCode
@InternalServerErrorResponseCode
public interface UserSessionApi {

    @Operation(
            summary = "Invalidate all user sessions",
            description = "Revokes all active refresh tokens associated with the specific user ID. This will force a logout on all devices."
    )
    @NoContentResponseCode
    @NotFoundResponseCode
    ResponseEntity<Void> invalidateAllSessions(
            @Parameter(description = "The UUID of the user whose sessions will be invalidated", required = true)
            UUID userId
    );

    @Operation(
            summary = "Invalidate a specific session",
            description = "Revokes a single session using its unique session ID. Useful for remote logout of a specific device."
    )
    @NoContentResponseCode
    @NotFoundResponseCode
    ResponseEntity<Void> invalidateSession(
            @Parameter(description = "The unique ID of the session to revoke", required = true)
            Long sessionId
    );

    @Operation(
            summary = "Get sessions by user ID",
            description = "Retrieves a paginated list of all active sessions for a specific user."
    )
    @OkResponseCode
    @NotFoundResponseCode
    ResponseEntity<PaginationWrapperResponseDTO<UserSessionResponseDTO>> getUserSessions(
            @Parameter(description = "The UUID of the user", required = true)
            UUID userId,
            @Parameter(description = "Pagination parameters (page, size, sort)")
            Pageable pageable
    );

    @Operation(
            summary = "Get all active sessions",
            description = "Retrieves a paginated list of all active sessions in the system. Typically reserved for administrative purposes."
    )
    @OkResponseCode
    ResponseEntity<PaginationWrapperResponseDTO<UserSessionResponseDTO>> getSessions(
            @Parameter(description = "Pagination parameters (page, size, sort)")
            Pageable pageable
    );

}
