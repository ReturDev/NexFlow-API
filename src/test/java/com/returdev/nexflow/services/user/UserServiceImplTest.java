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
import com.returdev.nexflow.utils.TestDtoFactory;
import com.returdev.nexflow.utils.TestEntityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private PasswordEncoder encoder;
    @Mock
    private UserRepository repository;
    @Mock
    private UserMapper mapper;
    @InjectMocks
    private UserServiceImpl service;

    @Test
    void getUserById_WhenUserExists_ReturnTheUser() {
        UserEntity entity = TestEntityFactory.createValidUser();
        UUID userId = entity.getId();
        UserResponseDTO expectedResponse = TestDtoFactory.createValidUserResponseDTO();

        when(repository.findById(userId)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(expectedResponse);

        UserResponseDTO result = service.getUserById(userId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(expectedResponse.id());
        assertThat(result.email()).isEqualTo(expectedResponse.email());

        verify(mapper).toResponse(entity);
        verify(repository).findById(userId);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {

        UUID userId = UUID.randomUUID();

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getUserById(userId));

        verify(mapper, never()).toResponse(any());
        verify(repository).findById(userId);
    }

    @Test
    void getUserByEmail_WhenUserExists_ReturnsTheUser() {
        String email = "email@email.com";
        UserEntity entity = TestEntityFactory.createValidUser();
        UserResponseDTO expectedResponse = TestDtoFactory.createValidUserResponseDTO();

        when(repository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(expectedResponse);

        UserResponseDTO result = service.getUserByEmail(email);


        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(expectedResponse.id());
        assertThat(result.email()).isEqualTo(expectedResponse.email());

        verify(mapper).toResponse(entity);
        verify(repository).findByEmail(email);

    }

    @Test
    void getUserByEmail_WhenUserNotExists_ShouldThrowException() {

        String email = "email@email.com";
        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getUserByEmail(email));


        verify(mapper, never()).toResponse(any());
        verify(repository).findByEmail(email);

    }

    @Test
    void saveUser_WithNewUniqueEmail_ShouldSaveTheUserWithEncodedPassword() {

        String email = "email@email.com";
        String encodedPassword = "encoded";
        UserEntity entity = TestEntityFactory.createValidUser();
        UserRequestDTO request = TestDtoFactory.createValidUserRequestDTO();
        UserResponseDTO expectedResponse = TestDtoFactory.createValidUserResponseDTO();

        when(mapper.toResponse(entity)).thenReturn(expectedResponse);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.existsByEmail(email)).thenReturn(false);
        when(repository.save(entity)).thenReturn(entity);
        when(encoder.encode(any())).thenReturn(encodedPassword);

        service.saveUser(request);

        assertThat(entity.getPassword()).isEqualTo(encodedPassword);

        verify(repository).existsByEmail(email);
        verify(encoder).encode(any());

    }

    @Test
    void saveUser_WithEmailRepeated_ShouldThrowException() {

        String email = "email@email.com";
        UserRequestDTO request = TestDtoFactory.createValidUserRequestDTO();

        when(repository.existsByEmail(email)).thenReturn(true);


        assertThrows(FieldAlreadyExistException.class, () -> service.saveUser(request));

        verify(repository).existsByEmail(email);
        verify(repository, never()).save(any());
        verify(mapper, never()).toEntity(any());
        verify(encoder, never()).encode(any());

    }

    @Test
    void updateUser_WhenIdExists_ReturnsUserUpdated() {

        UUID userId = UUID.randomUUID();
        UserEntity entity = TestEntityFactory.createValidUser();
        UserUpdateDTO updateDTO = TestDtoFactory.createValidUserUpdateDTO();
        UserResponseDTO expectedResponse = TestDtoFactory.createValidUserResponseDTO();

        when(repository.findById(userId)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(expectedResponse);

        UserResponseDTO result = service.updateUser(userId, updateDTO);

        assertThat(result.id()).isEqualTo(expectedResponse.id());
        assertThat(result.email()).isEqualTo(expectedResponse.email());

        verify(repository).findById(userId);
        verify(mapper).updateEntity(updateDTO, entity);
        verify(repository).save(entity);

    }

    @Test
    void updateUser_WhenIdNotExists_ShouldThrowException() {

        UUID userId = UUID.randomUUID();
        UserEntity entity = TestEntityFactory.createValidUser();
        UserUpdateDTO updateDTO = TestDtoFactory.createValidUserUpdateDTO();

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateUser(userId, updateDTO));

        verify(repository).findById(userId);
        verify(mapper, never()).updateEntity(updateDTO, entity);
        verify(repository, never()).save(entity);

    }

    @Test
    void updateUserPassword_WithValidData_ShouldEncodePasswordAndReturnUser() {

        UUID userId = UUID.randomUUID();
        PasswordUpdateDTO passwordUpdateDTO = TestDtoFactory.createValidPasswordUpdate();
        String newPasswordEncoded = "new_password_encoded";
        UserEntity entity = TestEntityFactory.createValidUser();
        String entityPasswordBeforeUpdate = entity.getPassword();

        when(repository.findById(userId)).thenReturn(Optional.of(entity));
        when(encoder.matches(passwordUpdateDTO.oldPassword(), entityPasswordBeforeUpdate)).thenReturn(true);
        when(encoder.encode(passwordUpdateDTO.newPassword())).thenReturn(newPasswordEncoded);

        service.updateUserPassword(userId, passwordUpdateDTO);

        assertThat(entity.getPassword())
                .isNotEqualTo(entityPasswordBeforeUpdate)
                .isEqualTo(newPasswordEncoded);

        verify(encoder).matches(passwordUpdateDTO.oldPassword(), entityPasswordBeforeUpdate);
        verify(repository).save(entity);

    }

    @Test
    void updateUserPassword_WithWrongOldPassword_ShouldThrowException() {

        UUID userId = UUID.randomUUID();
        PasswordUpdateDTO passwordUpdateDTO = TestDtoFactory.createValidPasswordUpdate();

        UserEntity entity = TestEntityFactory.createValidUser();

        when(encoder.matches(passwordUpdateDTO.oldPassword(), entity.getPassword())).thenReturn(false);
        when(repository.findById(userId)).thenReturn(Optional.of(entity));

        assertThrows(InvalidPasswordException.class, () -> service.updateUserPassword(userId, passwordUpdateDTO));

        verify(encoder, never()).encode(passwordUpdateDTO.newPassword());
        verify(repository, never()).save(entity);

    }

    @Test
    void updateUserPassword_WhenUserIdNotExists_ShouldThrowException() {

        UUID userId = UUID.randomUUID();
        PasswordUpdateDTO passwordUpdateDTO = TestDtoFactory.createValidPasswordUpdate();
        UserEntity entity = TestEntityFactory.createValidUser();

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateUserPassword(userId, passwordUpdateDTO));


        verify(encoder, never()).matches(any(), any());
        verify(encoder, never()).encode(passwordUpdateDTO.newPassword());
        verify(repository, never()).save(entity);

    }

    @Test
    void deleteUser_WhenIdExists_ShouldDeleteTheUser() {
        UUID userId = UUID.randomUUID();
        UserEntity entity = TestEntityFactory.createValidUser();

        when(repository.findById(userId)).thenReturn(Optional.of(entity));

        service.deleteUser(userId);

        verify(repository).findById(userId);
        verify(repository).delete(entity);

    }

    @Test
    void deleteUser_WhenIdNotExists_ShouldThrowException() {

        UUID userId = UUID.randomUUID();
        UserEntity entity = TestEntityFactory.createValidUser();

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteUser(userId));

        verify(repository).findById(userId);
        verify(repository, never()).delete(entity);

    }

}