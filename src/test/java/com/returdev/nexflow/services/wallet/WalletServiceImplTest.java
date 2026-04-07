package com.returdev.nexflow.services.wallet;

import com.returdev.nexflow.dto.request.WalletRequestDTO;
import com.returdev.nexflow.dto.request.update.WalletUpdateDTO;
import com.returdev.nexflow.dto.response.WalletResponseDTO;
import com.returdev.nexflow.mappers.WalletMapper;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.entities.WalletEntity;
import com.returdev.nexflow.model.exceptions.FieldAlreadyExistException;
import com.returdev.nexflow.model.exceptions.MaxWalletsReachedException;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
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
    @InjectMocks
    private WalletServiceImpl service;

    @Test
    void getWalletsOfUser_WhenUserExistsWithWallets_ReturnsListOfWallets() {

        UserEntity userEntity = TestEntityFactory.createValidUser();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(userEntity);
        WalletResponseDTO walletResponse = TestDtoFactory.createValidWalletResponseDTO();

        when(repository.findAllByUserId(userEntity.getId())).thenReturn(List.of(walletEntity));
        when(mapper.toResponse(walletEntity)).thenReturn(walletResponse);

        List<WalletResponseDTO> result = service.getWalletsOfUser(userEntity.getId());

        assertThat(result).hasSize(1)
                .first()
                .isEqualTo(walletResponse);

    }

    @Test
    void getWalletsOfUser_WhenUserNotExists_ReturnsEmptyList() {

        UUID userId = UUID.randomUUID();

        when(repository.findAllByUserId(userId)).thenReturn(List.of());

        List<WalletResponseDTO> result = service.getWalletsOfUser(userId);

        assertThat(result).hasSize(0);

    }

    @Test
    void getWalletById_WithExistingId_ReturnsWallet() {

        WalletEntity walletEntity = TestEntityFactory.createValidWallet(null);
        WalletResponseDTO expectedResponse = TestDtoFactory.createValidWalletResponseDTO();
        Long walletId = walletEntity.getId();

        when(repository.findById(walletId)).thenReturn(Optional.of(walletEntity));
        when(mapper.toResponse(walletEntity)).thenReturn(expectedResponse);

        WalletResponseDTO result = service.getWalletById(walletId);

        assertThat(result.id()).isEqualTo(expectedResponse.id());
        assertThat(result.name()).isEqualTo(expectedResponse.name());

        verify(mapper).toResponse(walletEntity);
        verify(repository).findById(walletId);

    }

    @Test
    void getWalletById_WhenIdNotExist_ShouldThrowException() {
        Long walletId = 1L;

        when(repository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getWalletById(walletId));


        verify(mapper, never()).toResponse(any());
        verify(repository).findById(walletId);

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
        WalletRequestDTO request = TestDtoFactory.createValidWalletRequestDTO(null);

        when(repository.existsByName(request.name())).thenReturn(false);
        when(repository.countByUserId(userEntity.getId())).thenReturn(0L);
        when(repository.save(walletEntity)).thenReturn(walletEntity);
        when(mapper.toEntity(request)).thenReturn(walletEntity);
        when(mapper.toResponse(walletEntity)).thenReturn(expectedResponse);

        WalletResponseDTO result = service.saveWallet(request);

        assertThat(result.id()).isEqualTo(expectedResponse.id());
        assertThat(result.name()).isEqualTo(expectedResponse.name());

        verify(repository).countByUserId(userEntity.getId());
        verify(repository).existsByName(request.name());
        verify(repository).save(walletEntity);

    }

    @Test
    void saveWallet_WithNameRepeated_ShouldThrowException() {

        WalletRequestDTO request = TestDtoFactory.createValidWalletRequestDTO(null);
        String name = request.name();

        when(repository.existsByName(name)).thenReturn(true);

        assertThrows(FieldAlreadyExistException.class, () -> service.saveWallet(request));

        verify(repository).existsByName(name);
        verify(repository, never()).countByUserId(any());
        verify(repository, never()).save(any());

    }

    @Test
    void saveWallet_WithWalletsLimitReached_ShouldThrowException() {

        UserEntity userEntity = TestEntityFactory.createValidUser();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(userEntity);
        WalletRequestDTO request = TestDtoFactory.createValidWalletRequestDTO(null);

        when(repository.existsByName(request.name())).thenReturn(false);
        when(repository.countByUserId(userEntity.getId())).thenReturn(10L);

        assertThrows(MaxWalletsReachedException.class, () -> service.saveWallet(request));

        verify(repository).countByUserId(userEntity.getId());
        verify(repository).existsByName(request.name());
        verify(repository, never()).save(walletEntity);

    }

    @Test
    void updateWallet_WhenIdExists_ReturnsWalletUpdated() {

        Long walletId = 1L;
        UserEntity userEntity = TestEntityFactory.createValidUser();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(userEntity);
        WalletResponseDTO expectedResponse = TestDtoFactory.createValidWalletResponseDTO();
        WalletUpdateDTO update = TestDtoFactory.createValidWalletUpdateDTO();

        when(repository.findById(walletId)).thenReturn(Optional.of(walletEntity));
        when(repository.save(walletEntity)).thenReturn(walletEntity);
        when(mapper.toResponse(walletEntity)).thenReturn(expectedResponse);

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

        when(repository.findById(walletId)).thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class, () -> service.updateWallet(walletId, update));

        verify(repository).findById(walletId);
        verify(repository, never()).save(walletEntity);
        verify(mapper, never()).toResponse(walletEntity);
        verify(mapper, never()).updateEntity(any(), any());
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

        assertThrows(ResourceNotFoundException.class, () -> service.incrementWalletBalance(walletId,balanceToIncrement));

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

        assertThrows(ResourceNotFoundException.class, () -> service.incrementWalletBalance(walletId,balanceToDecrement));

    }

    @Test
    void deleteWallet_WhenIdExists_ShouldDeleteTheUser() {

        Long walletId = 1L;
        WalletEntity entity = TestEntityFactory.createValidWallet(null);

        when(repository.findById(walletId)).thenReturn(Optional.of(entity));

        service.deleteWallet(walletId);

        verify(repository).findById(walletId);
        verify(repository).delete(entity);

    }


    @Test
    void deleteWallet_WhenIdNotExists_ShouldThrowException() {

        Long walletId = 1L;

        when(repository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteWallet(walletId));

        verify(repository).findById(walletId);
        verify(repository, never()).delete(any());

    }

}