package com.returdev.nexflow.mappers;

import com.returdev.nexflow.dto.request.UserRequestDTO;
import com.returdev.nexflow.dto.response.UserResponseDTO;
import com.returdev.nexflow.model.entities.UserEntity;

import org.springframework.stereotype.Component;

/**
 * Concrete implementation of {@link Mapper} for {@link UserEntity}.
 */
@Component
public class UserMapper implements Mapper<UserEntity, UserResponseDTO, UserRequestDTO> {

    /**
     * Maps a {@link UserRequestDTO} to a {@link UserEntity} using the Builder pattern.
     *
     * @param request the data transfer object containing user registration details.
     * @return a {@link UserEntity} instance populated with the request data.
     */
    @Override
    public UserEntity toEntity(UserRequestDTO request) {
        return UserEntity.builder()
                .name(request.name())
                .surnames(request.surnames())
                .email(request.email())
                .password(request.password())
                .build();
    }

    /**
     * Maps a {@link UserEntity} to a {@link UserResponseDTO}.
     *
     * @param entity the source user entity from the database.
     * @return a {@link UserResponseDTO} containing public profile and audit information.
     */
    @Override
    public UserResponseDTO toResponse(UserEntity entity) {
        return new UserResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getSurnames(),
                entity.getRole(),
                entity.getEmail(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
