package com.returdev.nexflow.controllers.user;

import com.returdev.nexflow.annotations.swagger.*;
import com.returdev.nexflow.dto.request.update.PasswordUpdateDTO;
import com.returdev.nexflow.dto.request.update.UserUpdateDTO;
import com.returdev.nexflow.dto.response.UserResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.ContentWrapperResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Users", description = "Endpoints for managing user profiles and security")
@SecurityRequirement(name = "Bearer Authentication")
@UnauthorizedResponseCode
@ForbiddenResponseCode
@InternalServerErrorResponseCode
public interface UserApi {

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves profile information for a user using their unique UUID."
    )
    @OkResponseCode
    @NotFoundResponseCode
    ResponseEntity<ContentWrapperResponseDTO<UserResponseDTO>> getUserById(
            @Parameter(description = "The UUID of the user", required = true) UUID id
    );

    @Operation(
            summary = "Get user by email",
            description = "Searches for a user profile using their email address."
    )
    @OkResponseCode
    @NotFoundResponseCode
    ResponseEntity<ContentWrapperResponseDTO<UserResponseDTO>> getUserByEmail(
            @Parameter(description = "The registered email address", required = true) String email
    );

    @Operation(
            summary = "Update user profile",
            description = "Modifies basic user information. Fields not provided will remain unchanged."
    )
    @OkResponseCode
    @BadRequestResponseCode
    @NotFoundResponseCode
    ResponseEntity<ContentWrapperResponseDTO<UserResponseDTO>> updateUser(
            @Parameter(description = "The UUID of the user to update", required = true) UUID id,
            UserUpdateDTO updateDTO
    );

    @Operation(
            summary = "Update user password",
            description = "Changes the user's password. Requires validation of the current credentials."
    )
    @NoContentResponseCode
    @BadRequestResponseCode
    @NotFoundResponseCode
    ResponseEntity<Void> updateUserPassword(
            @Parameter(description = "The UUID of the user", required = true) UUID id,
            PasswordUpdateDTO passwordUpdateDTO
    );

    @Operation(
            summary = "Delete user",
            description = "Permanently removes a user account and all associated data from the system."
    )
    @NoContentResponseCode
    @NotFoundResponseCode
    ResponseEntity<Void> deleteUser(
            @Parameter(description = "The UUID of the user to delete", required = true) UUID id
    );

}
