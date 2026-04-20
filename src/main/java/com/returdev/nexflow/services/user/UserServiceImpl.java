package com.returdev.nexflow.services.user;

import com.returdev.nexflow.dto.request.UserRequestDTO;
import com.returdev.nexflow.dto.request.update.PasswordUpdateDTO;
import com.returdev.nexflow.dto.request.update.UserUpdateDTO;
import com.returdev.nexflow.dto.response.UserResponseDTO;
import com.returdev.nexflow.mappers.UserMapper;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.enums.Role;
import com.returdev.nexflow.model.exceptions.FieldAlreadyExistException;
import com.returdev.nexflow.model.exceptions.InvalidPasswordException;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import com.returdev.nexflow.model.facade.AuthenticationFacade;
import com.returdev.nexflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of {@link UserService} using Spring Data JPA.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    private final AuthenticationFacade authenticationFacade;

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the user does not exist or access is denied.
     */
    @Override
    public UserResponseDTO getUserById(UUID id) {
        return mapper.toResponse(findByIdWithVerification(id));
    }


    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the user is not found or the requester lacks permission.
     */
    @Override
    public UserResponseDTO getUserByEmail(String email) {

        UserEntity authUser = authenticationFacade.getAuthenticateUser();

        if (authUser.getRole() != Role.ADMIN && !email.equals(authUser.getEmail())) {
            throw new ResourceNotFoundException("exception.user.email_not_found");
        }

        return mapper.toResponse(
                repository.findByEmail(email)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("exception.user.email_not_found")
                        )
        );
    }

    /**
     * {@inheritDoc}
     *
     * @throws FieldAlreadyExistException if the email is already registered in the system.
     */
    @Override
    @Transactional
    public UserEntity saveUser(UserRequestDTO user) {
        return repository.save(
                basicSaveConfiguration(user)
        );
    }

    @Override
    @Transactional
    public UserResponseDTO saveAdminUser(UserRequestDTO user) {

        UserEntity userEntity = basicSaveConfiguration(user);

        userEntity.setRole(Role.ADMIN);

        return mapper.toResponse(
                repository.save(userEntity)
        );
    }

    private UserEntity basicSaveConfiguration(UserRequestDTO user) {
        if (repository.existsByEmail(user.email())) {
            throw new FieldAlreadyExistException("exception.user.email_already_exists");
        }

        String encodedPassword = encoder.encode(user.password());
        UserEntity entity = mapper.toEntity(user);
        entity.setPassword(encodedPassword);

        return entity;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the user is not found or requester lacks permission.
     */
    @Override
    @Transactional
    public UserResponseDTO updateUser(UUID userId, UserUpdateDTO user) {

        UserEntity dbUser = findByIdWithVerification(userId);

        mapper.updateEntity(user, dbUser);

        return mapper.toResponse(
                repository.save(dbUser)
        );
    }

    /**
     * {@inheritDoc}
     *
     * @throws InvalidPasswordException  if the provided old password does not match the stored one.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @Override
    @Transactional
    public void updateUserPassword(UUID userId, PasswordUpdateDTO passwordUpdateDTO) {

        UserEntity dbUser = findByIdWithVerification(userId);

        if (!encoder.matches(passwordUpdateDTO.oldPassword(), dbUser.getPassword())) {
            throw new InvalidPasswordException("exception.security.change_password_mismatch");
        }

        String newPasswordEncoded = encoder.encode(passwordUpdateDTO.newPassword());

        dbUser.setPassword(
                newPasswordEncoded
        );

        repository.save(dbUser);

    }

    /**
     * {@inheritDoc}
     *
     * @param userId the identifier of the user to delete.
     * @throws ResourceNotFoundException if the user is not found or requester lacks permission.
     */
    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        UserEntity dbUser = findByIdWithVerification(userId);

        repository.delete(dbUser);
    }

    /**
     * Internal helper to find a user or throw a localized exception.
     *
     * @param id the user's UUID.
     * @return the {@link UserEntity}.
     * @throws ResourceNotFoundException if the user is not found.
     */
    private UserEntity findUserOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("exception.user.not_found"));
    }

    /**
     * Loads a user by their email for Spring Security authentication.
     *
     * @param email the user's email address.
     * @return the {@link UserDetails} for authentication.
     * @throws ResourceNotFoundException if the email is not found.
     */
    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws ResourceNotFoundException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("exception.user.not_found"));
    }

    /**
     * Verifies that the authenticated user has rights to access the target userId.
     *
     * @param userId the ID of the user record being accessed.
     * @return the found {@link UserEntity}.
     * @throws ResourceNotFoundException if the requester is neither the owner nor an admin.
     */
    private UserEntity findByIdWithVerification(UUID userId) {
        UserEntity authUser = authenticationFacade.getAuthenticateUser();

        if (authUser.getRole() != Role.ADMIN && !userId.equals(authUser.getId())) {
            throw new ResourceNotFoundException("exception.user.not_found");
        }

        return findUserOrThrow(userId);
    }

}
