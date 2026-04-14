package com.returdev.nexflow.services.wallet;

import com.returdev.nexflow.dto.request.WalletRequestDTO;
import com.returdev.nexflow.dto.request.update.WalletUpdateDTO;
import com.returdev.nexflow.dto.response.WalletResponseDTO;
import com.returdev.nexflow.mappers.WalletMapper;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.entities.WalletEntity;
import com.returdev.nexflow.model.enums.Role;
import com.returdev.nexflow.model.exceptions.FieldAlreadyExistException;
import com.returdev.nexflow.model.exceptions.MaxWalletsReachedException;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import com.returdev.nexflow.model.facade.AuthenticationFacade;
import com.returdev.nexflow.repositories.WalletRepository;
import com.returdev.nexflow.utils.TestDtoFactory;
import com.returdev.nexflow.utils.TestEntityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository repository;
    @Mock
    private WalletMapper mapper;
    @Mock
    private AuthenticationFacade authenticationFacade;
    @InjectMocks
    private WalletServiceImpl service;

    @Test
    void getWalletsOfUser_WhenUserExistsWithWallets_ReturnsPageOfWallets() {

        UserEntity userEntity = TestEntityFactory.createValidUser();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(userEntity);
        WalletResponseDTO walletResponse = TestDtoFactory.createValidWalletResponseDTO();
        Page<WalletEntity> page = new PageImpl<>(List.of(walletEntity));
        Pageable pageable = Pageable.ofSize(15);

        when(repository.findAllByUserId(userEntity.getId(), pageable)).thenReturn(page);
        when(mapper.toResponse(walletEntity)).thenReturn(walletResponse);
        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);

        Page<WalletResponseDTO> result = service.getWalletsOfUser(userEntity.getId(), pageable);

        assertThat(result).hasSize(1)
                .first()
                .isEqualTo(walletResponse);

    }

    @Test
    void getWalletsOfUser_WhenUserNotExists_ReturnsEmptyList() {

        UserEntity userEntity = TestEntityFactory.createValidUser();
        UUID userId = userEntity.getId();
        Pageable pageable = Pageable.ofSize(15);

        when(repository.findAllByUserId(userId, pageable)).thenReturn(Page.empty());
        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);

        Page<WalletResponseDTO> result = service.getWalletsOfUser(userId, pageable);

        assertThat(result).hasSize(0);

    }

    @Test
    void getWalletsOfUser_WhenUserIsNotOwnerEitherAdmin_ShouldThrowException() {

        Pageable pageable = Pageable.ofSize(15);
        UserEntity userEntity = TestEntityFactory.createValidUser();
        userEntity.setRole(Role.USER);
        UUID userIdProvided = UUID.randomUUID();

        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);

        assertThrows(ResourceNotFoundException.class, () -> service.getWalletsOfUser(userIdProvided, pageable));

        verify(authenticationFacade).getAuthenticateUser();
        verify(repository, never()).findAllByUserId(any(), any());

    }

    @Test
    void getWalletById_WithExistingId_ReturnsWallet() {

        UserEntity userEntity = TestEntityFactory.createValidUser();
        userEntity.setRole(Role.ADMIN);
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(null);
        WalletResponseDTO expectedResponse = TestDtoFactory.createValidWalletResponseDTO();
        Long walletId = walletEntity.getId();

        when(repository.findById(walletId)).thenReturn(Optional.of(walletEntity));
        when(mapper.toResponse(walletEntity)).thenReturn(expectedResponse);
        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);

        WalletResponseDTO result = service.getWalletById(walletId);

        assertThat(result.id()).isEqualTo(expectedResponse.id());
        assertThat(result.name()).isEqualTo(expectedResponse.name());

        verify(mapper).toResponse(walletEntity);
        verify(repository).findById(walletId);

    }

    @Test
    void getWalletById_WhenIdNotExist_ShouldThrowException() {
        Long walletId = 1L;
        UserEntity userEntity = TestEntityFactory.createValidUser();
        userEntity.setRole(Role.ADMIN);

        when(repository.findById(walletId)).thenReturn(Optional.empty());
        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);

        assertThrows(ResourceNotFoundException.class, () -> service.getWalletById(walletId));


        verify(mapper, never()).toResponse(any());
        verify(repository).findById(walletId);

    }

    @Test
    void getWalletById_WhenUserIsNotOwnerEitherAdmin_ShouldThrowException() {

        UserEntity userEntity = TestEntityFactory.createValidUser();
        userEntity.setRole(Role.USER);
        Long walletId = 1L;
        UUID userId = userEntity.getId();

        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);
        when(repository.findByIdAndUserId(walletId, userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getWalletById(walletId));

        verify(authenticationFacade).getAuthenticateUser();
        verify(repository).findByIdAndUserId(walletId, userId);
        verify(mapper, never()).toResponse(any());
    }

    @Test
    void getWallets_ReturnsPageOfWallets() {

        WalletEntity entity = TestEntityFactory.createValidWallet(null);
        WalletResponseDTO expectedResponse = TestDtoFactory.createValidWalletResponseDTO();
        Pageable pageable = PageRequest.of(0, 10);
        Page<WalletEntity> page = new PageImpl<>(List.of(entity));


        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.toResponse(entity)).thenReturn(expectedResponse);

        Page<WalletResponseDTO> result = service.getWallets(pageable);

        assertThat(result.getContent())
                .isNotNull()
                .hasSize(1)
                .first()
                .usingRecursiveAssertion()
                .isEqualTo(expectedResponse);

        verify(repository).findAll(pageable);
        verify(mapper).toResponse(entity);

    }

    @Test
    void saveWallet_WithNewUniqueNameAndLimitDoesNotReached_ReturnsSavedWallet() {

        UserEntity userEntity = TestEntityFactory.createValidUser();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(userEntity);
        WalletResponseDTO expectedResponse = TestDtoFactory.createValidWalletResponseDTO();
        WalletRequestDTO request = TestDtoFactory.createValidWalletRequestDTO(userEntity.getId());

        when(repository.existsByName(request.name())).thenReturn(false);
        when(repository.countByUserId(userEntity.getId())).thenReturn(0L);
        when(repository.save(walletEntity)).thenReturn(walletEntity);
        when(mapper.toEntity(request)).thenReturn(walletEntity);
        when(mapper.toResponse(walletEntity)).thenReturn(expectedResponse);
        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);

        WalletResponseDTO result = service.saveWallet(request);

        assertThat(result.id()).isEqualTo(expectedResponse.id());
        assertThat(result.name()).isEqualTo(expectedResponse.name());

        verify(repository).countByUserId(userEntity.getId());
        verify(repository).existsByName(request.name());
        verify(repository).save(walletEntity);

    }

    @Test
    void saveWallet_WithNameRepeated_ShouldThrowException() {

        UserEntity userEntity = TestEntityFactory.createValidUser();
        WalletRequestDTO request = TestDtoFactory.createValidWalletRequestDTO(userEntity.getId());
        String name = request.name();

        when(repository.existsByName(name)).thenReturn(true);
        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);

        assertThrows(FieldAlreadyExistException.class, () -> service.saveWallet(request));

        verify(repository).existsByName(name);
        verify(repository, never()).countByUserId(any());
        verify(repository, never()).save(any());

    }

    @Test
    void saveWallet_WithWalletsLimitReached_ShouldThrowException() {

        UserEntity userEntity = TestEntityFactory.createValidUser();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(userEntity);
        WalletRequestDTO request = TestDtoFactory.createValidWalletRequestDTO(userEntity.getId());

        when(repository.existsByName(request.name())).thenReturn(false);
        when(repository.countByUserId(userEntity.getId())).thenReturn(10L);
        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);

        assertThrows(MaxWalletsReachedException.class, () -> service.saveWallet(request));

        verify(repository).countByUserId(userEntity.getId());
        verify(repository).existsByName(request.name());
        verify(repository, never()).save(walletEntity);

    }

    @Test
    void saveWallet_WhenUserIsNotOwnerEitherAdmin_ShouldThrowException() {

        UserEntity userEntity = TestEntityFactory.createValidUser();
        WalletRequestDTO requestDTO = TestDtoFactory.createValidWalletRequestDTO(UUID.randomUUID());

        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);

        assertThrows(ResourceNotFoundException.class, () -> service.saveWallet(requestDTO));

        verify(authenticationFacade).getAuthenticateUser();
        verify(repository, never()).save(any());

    }

    @Test
    void updateWallet_WhenIdExists_ReturnsWalletUpdated() {

        Long walletId = 1L;
        UserEntity userEntity = TestEntityFactory.createValidUser();
        userEntity.setRole(Role.ADMIN);
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(userEntity);
        WalletResponseDTO expectedResponse = TestDtoFactory.createValidWalletResponseDTO();
        WalletUpdateDTO update = TestDtoFactory.createValidWalletUpdateDTO();

        when(repository.findById(walletId)).thenReturn(Optional.of(walletEntity));
        when(repository.save(walletEntity)).thenReturn(walletEntity);
        when(mapper.toResponse(walletEntity)).thenReturn(expectedResponse);
        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);

        WalletResponseDTO result = service.updateWallet(walletId, update);

        assertThat(result.id()).isEqualTo(expectedResponse.id());
        assertThat(result.name()).isEqualTo(expectedResponse.name());

        verify(repository).findById(walletId);
        verify(repository).save(walletEntity);

    }

    @Test
    void updateWallet_WhenIdNotExists_ShouldThrowException() {
        Long walletId = 1L;
        WalletUpdateDTO update = TestDtoFactory.createValidWalletUpdateDTO();
        UserEntity userEntity = TestEntityFactory.createValidUser();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(userEntity);

        when(repository.findByIdAndUserId(walletId, userEntity.getId())).thenReturn(Optional.empty());
        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);

        assertThrows(ResourceNotFoundException.class, () -> service.updateWallet(walletId, update));

        verify(repository).findByIdAndUserId(walletId,userEntity.getId());
        verify(repository, never()).save(walletEntity);
        verify(mapper, never()).toResponse(walletEntity);
        verify(mapper, never()).updateEntity(any(), any());
    }

    @Test
    void updateWallet_WhenUserIsNotOwnerEitherAdmin_ShouldThrowException() {

        Long walletId = 1L;
        UserEntity userEntity = TestEntityFactory.createValidUser();
        userEntity.setRole(Role.USER);
        WalletUpdateDTO updateDTO = TestDtoFactory.createValidWalletUpdateDTO();

        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);
        when(repository.findByIdAndUserId(walletId, userEntity.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateWallet(
                walletId,
                updateDTO
        ));

        verify(authenticationFacade).getAuthenticateUser();
        verify(repository, never()).save(any());

    }

    @Test
    void incrementWalletBalance_WhenWalletIdExists_ShouldIncrementWalletBalance() {

        Long walletId = 1L;
        Long balanceToIncrement = 100L;
        WalletEntity entity = TestEntityFactory.createValidWallet(null);
        Long walletBalanceBeforeIncrement = entity.getBalanceInCents();
        Long balanceSum = walletBalanceBeforeIncrement + balanceToIncrement;

        when(repository.findById(walletId)).thenReturn(Optional.of(entity));

        service.incrementWalletBalance(walletId, balanceToIncrement);

        assertThat(entity.getBalanceInCents()).isNotEqualTo(walletBalanceBeforeIncrement).isEqualTo(balanceSum);

    }

    @Test
    void incrementWalletBalance_WhenWalletIdNotExists_ShouldThrowException() {

        Long walletId = 1L;
        Long balanceToIncrement = 100L;
        when(repository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.incrementWalletBalance(walletId, balanceToIncrement));

    }

    @Test
    void decrementWalletBalance_WhenWalletIdExists_ShouldIncrementWalletBalance() {

        Long walletId = 1L;
        Long balanceToDecrement = 100L;

        WalletEntity entity = TestEntityFactory.createValidWallet(null);
        Long walletBalanceBeforeDecrement = entity.getBalanceInCents();
        Long balanceSubstraction = walletBalanceBeforeDecrement - balanceToDecrement;

        when(repository.findById(walletId)).thenReturn(Optional.of(entity));

        service.decrementWalletBalance(walletId, balanceToDecrement);

        assertThat(entity.getBalanceInCents()).isNotEqualTo(walletBalanceBeforeDecrement).isEqualTo(balanceSubstraction);

    }

    @Test
    void decrementWalletBalance_WhenWalletIdNotExists_ShouldThrowException() {

        Long walletId = 1L;
        Long balanceToDecrement = 100L;
        when(repository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.incrementWalletBalance(walletId, balanceToDecrement));

    }

    @Test
    void deleteWallet_WhenIdExists_ShouldDeleteTheUser() {

        Long walletId = 1L;
        UserEntity userEntity = TestEntityFactory.createValidUser();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(userEntity);

        when(repository.findByIdAndUserId(walletId, userEntity.getId())).thenReturn(Optional.of(walletEntity));
        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);

        service.deleteWallet(walletId);

        verify(repository).findByIdAndUserId(walletId, userEntity.getId());
        verify(repository).delete(walletEntity);

    }


    @Test
    void deleteWallet_WhenIdNotExists_ShouldThrowException() {

        Long walletId = 1L;
        UserEntity userEntity = TestEntityFactory.createValidUser();
        userEntity.setRole(Role.ADMIN);
        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);

        when(repository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteWallet(walletId));

        verify(repository).findById(walletId);
        verify(repository, never()).delete(any());

    }

    @Test
    void deleteWallet_WhenUserIsNotOwnerEitherAdmin_ShouldThrowException() {

        Long walletId = 1L;
        UserEntity userEntity = TestEntityFactory.createValidUser();
        when(authenticationFacade.getAuthenticateUser()).thenReturn(userEntity);

        when(repository.findByIdAndUserId(walletId, userEntity.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteWallet(walletId));

        verify(repository).findByIdAndUserId(walletId, userEntity.getId());
        verify(repository, never()).delete(any());

    }

    @Test
    void verifyExistsWalletOfUser_WhenWalletDoesNotExist_ShouldThrowException() {

        Long walletId = 1L;
        UUID userId = UUID.randomUUID();

        when(repository.existsByIdAndUserId(walletId, userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.verifyExistsWalletOfUser(walletId, userId));

        verify(repository).existsByIdAndUserId(walletId, userId);

    }

}