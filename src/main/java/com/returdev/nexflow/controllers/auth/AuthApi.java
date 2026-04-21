package com.returdev.nexflow.controllers.auth;

import com.returdev.nexflow.annotations.swagger.*;
import com.returdev.nexflow.dto.request.AuthRequestDTO;
import com.returdev.nexflow.dto.request.TokenRequestDTO;
import com.returdev.nexflow.dto.request.UserRequestDTO;
import com.returdev.nexflow.dto.response.AuthResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;

@InternalServerErrorResponseCode
@Tag(name = "Authentication", description = "Endpoints for user authentication and token management")
public interface AuthApi {

    @SecurityRequirements
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account in the system and returns authentication tokens."
    )
    @BadRequestResponseCode
    @ConflictResponseCode
    @CreatedResponseCode
    ResponseEntity<AuthResponseDTO> signup(
            @Valid UserRequestDTO userRequestDTO,
            @Parameter(hidden = true) HttpServletRequest request);

    @Operation(
            summary = "Authenticate user",
            description = "Validates user credentials and returns a pair of Access and Refresh tokens."
    )
    @NotFoundResponseCode
    @OkResponseCode
    @SecurityRequirements
    ResponseEntity<AuthResponseDTO> login(
            @Valid AuthRequestDTO authRequestDTO,
            @Parameter(hidden = true) HttpServletRequest request);

    @Operation(
            summary = "Refresh access token",
            description = "Exchanges a valid refresh token for a new access token."
    )
    @OkResponseCode
    @ForbiddenResponseCode
    @SecurityRequirements
    ResponseEntity<AuthResponseDTO> refresh(
            @Valid TokenRequestDTO request
    );

    @Operation(
            summary = "Logout user",
            description = "Invalidates the provided refresh token, effectively logging the user out.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @NoContentResponseCode
    @BadRequestResponseCode
    ResponseEntity<Void> logout(
            @Valid TokenRequestDTO request
    );

    @Operation(
            summary = "Invalidate all user sessions",
            description = "Revokes all active refresh tokens associated with the user's email address.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @NoContentResponseCode
    @NotFoundResponseCode
    @UnauthorizedResponseCode
    ResponseEntity<Void> invalidateAllSessions(
            @Parameter(description = "The email of the user whose sessions will be invalidated", required = true)
            @Email(message = "{validation.email.invalid}") String email
    );

}
