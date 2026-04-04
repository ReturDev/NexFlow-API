package com.returdev.nexflow.mappers;

import com.returdev.nexflow.dto.request.UserRequestDTO;
import com.returdev.nexflow.dto.request.update.UserUpdateDTO;
import com.returdev.nexflow.dto.response.UserResponseDTO;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.utils.TestDtoFactory;
import com.returdev.nexflow.utils.TestEntityFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toEntity_ShouldMapAllFields() {

        UserRequestDTO request = TestDtoFactory.createValidUserRequestDTO();

        UserEntity entity = mapper.toEntity(request);


        assertThat(entity.getName()).isEqualTo(request.name());
        assertThat(entity.getSurnames()).isEqualTo(request.surnames());
        assertThat(entity.getEmail()).isEqualTo(request.email());
        assertThat(entity.getPassword()).isEqualTo(request.password());

    }

    @Test
    void toResponse_ShouldMapAllFields() {

        UserEntity entity = TestEntityFactory.createValidUser();

        UserResponseDTO response = mapper.toResponse(entity);

        assertThat(response.id()).isEqualTo(entity.getId());
        assertThat(response.name()).isEqualTo(entity.getName());
        assertThat(response.surnames()).isEqualTo(entity.getSurnames());
        assertThat(response.email()).isEqualTo(entity.getEmail());
        assertThat(response.role()).isEqualTo(entity.getRole());
        assertThat(response.createdAt()).isEqualTo(entity.getCreatedAt());
        assertThat(response.updatedAt()).isEqualTo(entity.getUpdatedAt());

    }

    @Test
    void updateEntity_WithAllFields_UpdatesCorrectFields() {

        UserEntity entity = TestEntityFactory.createValidUser();

        UserUpdateDTO dto = TestDtoFactory.createValidUserUpdateDTO();

        mapper.updateEntity(dto, entity);

        assertThat(entity.getName()).isEqualTo(dto.name());
        assertThat(entity.getSurnames()).isEqualTo(dto.surnames());

    }

    @Test
    void updateEntity_WithNullFields_DoesNotOverride() {

        String originalName = "Name";
        String originalSurnames = "Surname";

        UserEntity entity = UserEntity.builder()
                .name(originalName)
                .surnames(originalSurnames)
                .build();

        UserUpdateDTO dto = new UserUpdateDTO(
                null,
                null
        );

        mapper.updateEntity(dto, entity);

        assertThat(entity.getName()).isEqualTo(originalName);
        assertThat(entity.getSurnames()).isEqualTo(originalSurnames);

    }
}