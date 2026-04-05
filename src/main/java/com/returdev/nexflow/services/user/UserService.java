package com.returdev.nexflow.services.user;

import com.returdev.nexflow.dto.request.UserRequestDTO;
import com.returdev.nexflow.dto.request.update.UserUpdateDTO;
import com.returdev.nexflow.dto.response.UserResponseDTO;
import com.returdev.nexflow.model.exceptions.FieldAlreadyExistException;
import com.returdev.nexflow.model.exceptions.InvalidPasswordException;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

/**
 * Service interface for User management and Identity logic.
 */
@Validated
public interface UserService {

    /**
     * Retrieves a user profile by their unique email address.
     *
     * @param email the email associated with the account.
     * @return the found {@link UserResponseDTO}.
     * @throws ResourceNotFoundException if no user exists with the provided email.
     */
    UserResponseDTO getUserByEmail(String email);

    /**
     * Registers a new user in the system.
     *
     * @param user the registration data, including plain-text password.
     * @return the persisted {@link UserResponseDTO}.
     * @throws FieldAlreadyExistException if the email address is already in use.
     */
    UserResponseDTO saveUser(@Valid UserRequestDTO user);

    /**
     * Updates an existing user's profile information.
     *
     * @param userId the unique identifier (UUID) of the user.
     * @param user   the update data (partial updates supported).
     * @return the modified {@link UserResponseDTO}.
     * @throws ResourceNotFoundException if the user ID is not found.
     */
    UserResponseDTO updateUser(UUID userId, @Valid UserUpdateDTO user);

    /**
     * Updates the authenticated user's credentials after verifying their current password.
     *
     * @param oldPassword the current plain-text password for identity verification.
     * @param newPassword    the new plain-text password to be set.
     * @throws InvalidPasswordException  if the {@code oldPassword} does not match the
     *                                   stored credential.
     * @throws ResourceNotFoundException if the user ID is not found.
     */
    void updateUserPassword(
            UUID userId,
            String oldPassword,
            String newPassword
    );

    /**
     * Permanently removes a user account from the system.
     *
     * @param userId the unique identifier of the user to delete.
     * @throws ResourceNotFoundException if the user ID is not found.
     */
    void deleteUser(UUID userId);
}
