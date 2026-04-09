package com.returdev.nexflow.services.user;

import com.returdev.nexflow.dto.request.UserRequestDTO;
import com.returdev.nexflow.dto.request.update.PasswordUpdateDTO;
import com.returdev.nexflow.dto.request.update.UserUpdateDTO;
import com.returdev.nexflow.dto.response.UserResponseDTO;
import com.returdev.nexflow.model.exceptions.FieldAlreadyExistException;
import com.returdev.nexflow.model.exceptions.InvalidPasswordException;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

/**
 * Service interface for User management and Identity logic.
 */
@Validated
public interface UserService {

    /**
     * Retrieves the profile information of a specific user.
     *
     * @param id the globally unique identifier (UUID) of the user.
     * @return a {@link UserResponseDTO} containing the user's public and profile data.
     * @throws ResourceNotFoundException if no user exists with the provided {@code id},
     *                                   using the {@code "exception.user.not_found"} message key.
     */
    UserResponseDTO getUserById(UUID id);

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
     * Updates a user's password after verifying their current credentials.
     *
     * @param userId            the globally unique identifier of the user.
     * @param passwordUpdateDTO the validated credentials containing the old and new passwords.
     * @throws InvalidPasswordException  if the old password does not match or if
     *                                   validation constraints are violated.
     * @throws ResourceNotFoundException if no user is found with the given UUID.
     */
    void updateUserPassword(
            UUID userId,
            @Valid PasswordUpdateDTO passwordUpdateDTO
    );

    /**
     * Permanently removes a user account from the system.
     *
     * @param userId the unique identifier of the user to delete.
     * @throws ResourceNotFoundException if the user ID is not found.
     */
    void deleteUser(UUID userId);
}
