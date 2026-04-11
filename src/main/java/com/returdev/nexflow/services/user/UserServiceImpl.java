package com.returdev.nexflow.services.user;

import com.returdev.nexflow.dto.request.UserRequestDTO;
import com.returdev.nexflow.dto.request.update.PasswordUpdateDTO;
import com.returdev.nexflow.dto.request.update.UserUpdateDTO;
import com.returdev.nexflow.dto.response.UserResponseDTO;
import com.returdev.nexflow.mappers.UserMapper;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.exceptions.FieldAlreadyExistException;
import com.returdev.nexflow.model.exceptions.InvalidPasswordException;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import com.returdev.nexflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO getUserById(UUID id) {
        return mapper.toResponse(findUserOrThrow(id));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO getUserByEmail(String email) {
        return mapper.toResponse(
                repository.findByEmail(email)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("exception.user.email_not_found")
                        )
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserEntity saveUser(UserRequestDTO user) {

        if (repository.existsByEmail(user.email())) {
            throw new FieldAlreadyExistException("exception.user.email_already_exists");
        }

        String encodedPassword = encoder.encode(user.password());
        UserEntity entity = mapper.toEntity(user);
        entity.setPassword(encodedPassword);

        return repository.save(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserResponseDTO updateUser(UUID userId, UserUpdateDTO user) {

        UserEntity dbUser = findUserOrThrow(userId);

        mapper.updateEntity(user, dbUser);

        return mapper.toResponse(
                repository.save(dbUser)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateUserPassword(UUID userId, PasswordUpdateDTO passwordUpdateDTO) {
        UserEntity dbUser = findUserOrThrow(userId);

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
     */
    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        UserEntity dbUser = findUserOrThrow(userId);

        repository.delete(dbUser);
    }

    /**
     * Internal helper to find a user or throw a localized exception.
     *
     * @param id the user's UUID.
     * @return the {@link UserEntity}.
     */
    private UserEntity findUserOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("exception.user.not_found"));
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws ResourceNotFoundException {
        return repository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("exception.user.not_found"));
    }

}
