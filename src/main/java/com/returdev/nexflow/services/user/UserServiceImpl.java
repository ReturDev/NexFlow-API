package com.returdev.nexflow.services.user;

import com.returdev.nexflow.dto.request.UserRequestDTO;
import com.returdev.nexflow.dto.request.update.UserUpdateDTO;
import com.returdev.nexflow.dto.response.UserResponseDTO;
import com.returdev.nexflow.mappers.UserMapper;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.exceptions.BusinessException;
import com.returdev.nexflow.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
    public UserResponseDTO getUserByEmail(String email) {
        return mapper.toResponse(
                repository.findByEmail(email)
                        .orElseThrow(
                                () -> new EntityNotFoundException("exception.user.email_not_found")
                        )
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserResponseDTO saveUser(UserRequestDTO user) {

        if (repository.existsByEmail(user.email())) {
            throw new BusinessException("exception.user.email_already_exists");
        }

        String encodedPassword = encoder.encode(user.password());
        UserEntity entity = mapper.toEntity(user);
        entity.setPassword(encodedPassword);

        return mapper.toResponse(
                repository.save(entity)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserResponseDTO updateUser(UUID userId, UserUpdateDTO user) {

        UserEntity dbEntity = findUserOrThrow(userId);

        mapper.updateEntity(user, dbEntity);

        if (user.password() != null) {
            String encodedPassword = encoder.encode(user.password());
            dbEntity.setPassword(encodedPassword);
        }

        return mapper.toResponse(
                repository.save(dbEntity)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        UserEntity dbEntity = findUserOrThrow(userId);

        repository.delete(dbEntity);
    }

    /**
     * Internal helper to find a user or throw a localized exception.
     *
     * @param id the user's UUID.
     * @return the {@link UserEntity}.
     */
    private UserEntity findUserOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("exception.user.not_found"));
    }

}
