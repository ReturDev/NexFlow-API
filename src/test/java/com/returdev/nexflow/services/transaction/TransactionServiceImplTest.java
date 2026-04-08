package com.returdev.nexflow.services.transaction;

import com.returdev.nexflow.dto.request.TransactionRequestDTO;
import com.returdev.nexflow.dto.request.update.TransactionUpdateDTO;
import com.returdev.nexflow.dto.response.TransactionResponseDTO;
import com.returdev.nexflow.mappers.TransactionMapper;
import com.returdev.nexflow.model.entities.CategoryEntity;
import com.returdev.nexflow.model.entities.TransactionEntity;
import com.returdev.nexflow.model.entities.WalletEntity;
import com.returdev.nexflow.model.enums.TransactionType;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import com.returdev.nexflow.repositories.TransactionRepository;
import com.returdev.nexflow.services.category.CategoryService;
import com.returdev.nexflow.services.wallet.WalletService;
import com.returdev.nexflow.utils.TestDtoFactory;
import com.returdev.nexflow.utils.TestEntityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private CategoryService categoryService;
    @Mock
    private WalletService walletService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionMapper mapper;
    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void getTransactionById_WhenTransactionExists_ReturnsTheTransaction() {

        Long transactionId = 1L;
        TransactionEntity entity = TestEntityFactory.createValidTransaction(null, null, null);
        TransactionResponseDTO expectedResponse = TestDtoFactory.createValidTransactionResponseDTO(null, 1L, 1L);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(expectedResponse);

        TransactionResponseDTO result = transactionService.getTransactionById(transactionId);

        assertThat(result.id())
                .isEqualTo(expectedResponse.id());
        assertThat(result.title())
                .isEqualTo(expectedResponse.title());
        assertThat(result.balanceInCents())
                .isEqualTo(expectedResponse.balanceInCents());

    }

    @Test
    void getTransactionById_WhenTransactionNotExists_ShouldThrowException() {

        Long transactionId = 1L;

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionById(transactionId));

        verify(mapper, never()).toResponse(any());
        verify(transactionRepository).findById(transactionId);

    }

    @Test
    void getTransactionsByWalletId_WhenWalletExists_ReturnsWalletTransactionsPages() {

        Long walletId = 1L;
        Pageable pageable = Pageable.ofSize(20);

        TransactionEntity entity = TestEntityFactory.createValidTransaction(null, null, null);
        TransactionResponseDTO response = TestDtoFactory.createValidTransactionResponseDTO(null, walletId, null);
        Page<TransactionEntity> page = new PageImpl<>(List.of(entity));

        when(transactionRepository.findByWalletId(walletId, pageable)).thenReturn(page);
        when(mapper.toResponse(entity)).thenReturn(response);

        Page<TransactionResponseDTO> result = transactionService.getTransactionsByWalletId(walletId, pageable);

        assertThat(result.getContent())
                .isNotNull()
                .hasSize(1)
                .first()
                .usingRecursiveAssertion()
                .isEqualTo(response);

        verify(transactionRepository).findByWalletId(walletId, pageable);
        verify(mapper).toResponse(entity);

    }

    @Test
    void getTransactionsByWalletId_WhenWalletDoesNotExists_ReturnsEmptyPage() {

        Long walletId = 1L;
        Pageable pageable = Pageable.ofSize(20);

        Page<TransactionEntity> page = new PageImpl<>(List.of());

        when(transactionRepository.findByWalletId(walletId, pageable)).thenReturn(page);

        Page<TransactionResponseDTO> result = transactionService.getTransactionsByWalletId(walletId, pageable);

        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(transactionRepository).findByWalletId(walletId, pageable);
        verify(mapper, never()).toResponse(any());

    }

    @Test
    void getTransactions_ReturnTransactionPage() {
        Pageable pageable = Pageable.ofSize(20);

        TransactionEntity entity = TestEntityFactory.createValidTransaction(null, null, null);
        TransactionResponseDTO expectedResponse = TestDtoFactory.createValidTransactionResponseDTO(null, null, null);
        Page<TransactionEntity> page = new PageImpl<>(List.of(entity));

        when(mapper.toResponse(entity)).thenReturn(expectedResponse);
        when(transactionRepository.findAll(pageable)).thenReturn(page);

        Page<TransactionResponseDTO> result = transactionService.getTransactions(pageable);

        assertThat(result)
                .isNotNull()
                .hasSize(1)
                .first()
                .usingRecursiveAssertion()
                .isEqualTo(expectedResponse);

    }

    @Test
    void saveTransaction_WithAllFieldsValid_ShouldIncrementWalletBalanceAndReturnsSavedTransaction() {
        Long categoryId = 1L;
        Long walletId = 1L;

        TransactionRequestDTO request = TestDtoFactory.createValidTransactionRequestDTO(categoryId, walletId);
        TransactionResponseDTO expectedResponse = TestDtoFactory.createValidTransactionResponseDTO(null, walletId, null);
        TransactionEntity entity = TestEntityFactory.createValidTransaction(null, null, null);

        when(mapper.toResponse(entity)).thenReturn(expectedResponse);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(transactionRepository.save(entity)).thenReturn(entity);

        TransactionResponseDTO result = transactionService.saveTransaction(request);

        assertThat(result)
                .usingRecursiveAssertion()
                .isEqualTo(expectedResponse);

        verify(walletService).incrementWalletBalance(any(), any());
        verify(walletService, never()).decrementWalletBalance(any(), any());
        verify(transactionRepository).save(entity);

    }

    @Test
    void saveTransaction_WithAllFieldsValid_ShouldDecrementWalletBalanceAndReturnsSavedTransaction() {
        Long categoryId = 1L;
        Long walletId = 1L;

        TransactionRequestDTO request = new TransactionRequestDTO(
                "title",
                "description",
                1L,
                TransactionType.EXPENSE,
                null,
                categoryId,
                walletId
        );
        TransactionResponseDTO expectedResponse = TestDtoFactory.createValidTransactionResponseDTO(null, walletId, null);
        TransactionEntity entity = TestEntityFactory.createValidTransaction(null, null, null);

        when(mapper.toResponse(entity)).thenReturn(expectedResponse);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(transactionRepository.save(entity)).thenReturn(entity);

        TransactionResponseDTO result = transactionService.saveTransaction(request);

        assertThat(result)
                .usingRecursiveAssertion()
                .isEqualTo(expectedResponse);

        verify(walletService, never()).incrementWalletBalance(any(), any());
        verify(walletService).decrementWalletBalance(any(), any());
        verify(transactionRepository).save(entity);

    }

    @Test
    void saveTransaction_WithCategoryInvalid_ShouldThrowException() {
        Long categoryId = 1L;
        Long walletId = 1L;

        TransactionRequestDTO request = TestDtoFactory.createValidTransactionRequestDTO(categoryId, walletId);

        doThrow(ResourceNotFoundException.class).when(categoryService).verifyCategoryExists(walletId);

        assertThrows(ResourceNotFoundException.class, () -> transactionService.saveTransaction(request));

        verify(walletService, never()).incrementWalletBalance(any(), any());
        verify(walletService, never()).decrementWalletBalance(any(), any());
        verify(transactionRepository, never()).save(any());

    }

    @Test
    void saveTransaction_WithWalletIdInvalid_ShouldThrowException() {
        Long categoryId = 1L;
        Long walletId = 1L;

        TransactionRequestDTO request = TestDtoFactory.createValidTransactionRequestDTO(categoryId, walletId);

        doThrow(ResourceNotFoundException.class).when(walletService).incrementWalletBalance(any(), any());
        assertThrows(ResourceNotFoundException.class, () -> transactionService.saveTransaction(request));

        verify(walletService).incrementWalletBalance(any(), any());
        verify(walletService, never()).decrementWalletBalance(any(), any());
        verify(transactionRepository, never()).save(any());

    }

    @Test
    void saveTransactionFromPlan_WhenAllFieldsValid_ShouldSaveTheTransaction() {

        CategoryEntity categoryEntity = TestEntityFactory.createValidCategory();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(null);
        TransactionEntity transactionEntity = TestEntityFactory.createValidTransaction(categoryEntity, walletEntity, null);


        transactionService.saveTransactionFromPlan(transactionEntity);


        verify(walletService).decrementWalletBalance(any(), any());
        verify(walletService, never()).incrementWalletBalance(any(), any());
        verify(transactionRepository).save(transactionEntity);

    }

    @Test
    void saveTransactionFromPlan_WithCategoryInvalid_ShouldThrowException() {

        CategoryEntity categoryEntity = TestEntityFactory.createValidCategory();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(null);
        TransactionEntity transactionEntity = TestEntityFactory.createValidTransaction(categoryEntity, walletEntity, null);

        doThrow(ResourceNotFoundException.class).when(categoryService).verifyCategoryExists(categoryEntity.getId());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.saveTransactionFromPlan(transactionEntity));

        verify(walletService, never()).decrementWalletBalance(any(), any());
        verify(walletService, never()).incrementWalletBalance(any(), any());
        verify(transactionRepository, never()).save(transactionEntity);

    }

    @Test
    void saveTransactionFromPlan_WithWalletIdInvalid_ShouldThrowException() {

        CategoryEntity categoryEntity = TestEntityFactory.createValidCategory();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(null);
        TransactionEntity transactionEntity = TestEntityFactory.createValidTransaction(categoryEntity, walletEntity, null);

        doThrow(ResourceNotFoundException.class).when(walletService).decrementWalletBalance(walletEntity.getId(), transactionEntity.getBalanceInCents());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.saveTransactionFromPlan(transactionEntity));

        verify(walletService).decrementWalletBalance(any(), any());
        verify(walletService, never()).incrementWalletBalance(any(), any());
        verify(transactionRepository, never()).save(transactionEntity);

    }

    @Test
    void updateTransaction_WhenBalanceChanges_ShouldUpdateWalletBalanceAndReturnsUpdatedTransaction() {
        Long id = 1L;
        Long walletId = 10L;

        CategoryEntity categoryEntity = TestEntityFactory.createValidCategory();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(null);
        walletEntity.setId(walletId);
        TransactionEntity transactionEntity = TestEntityFactory.createValidTransaction(categoryEntity, walletEntity, null);
        TransactionUpdateDTO update = new TransactionUpdateDTO(
                "New title",
                "New description",
                100L,
                transactionEntity.getType(),
                OffsetDateTime.now().plusDays(1),
                null
        );
        TransactionResponseDTO expectedResponse = TestDtoFactory.createValidTransactionResponseDTO(null, walletId, null);

        when(transactionRepository.findById(id)).thenReturn(Optional.of(transactionEntity));
        when(transactionRepository.save(transactionEntity)).thenReturn(transactionEntity);
        when(mapper.toResponse(transactionEntity)).thenReturn(expectedResponse);


        transactionService.updateTransaction(id, update);


        verify(walletService).incrementWalletBalance(walletId, transactionEntity.getBalanceInCents());
        verify(walletService).decrementWalletBalance(walletId, update.balanceInCents());
        verify(mapper).updateEntity(update, transactionEntity);
    }

    @Test
    void updateTransaction_WhenBalanceOrTypeDoesNotChange_ShouldNotUpdateWalletBalanceAndReturnsUpdatedTransaction() {
        Long id = 1L;
        Long walletId = 10L;

        CategoryEntity categoryEntity = TestEntityFactory.createValidCategory();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(null);
        walletEntity.setId(walletId);
        TransactionEntity transactionEntity = TestEntityFactory.createValidTransaction(categoryEntity, walletEntity, null);
        TransactionUpdateDTO update = new TransactionUpdateDTO(
                "New title",
                "New description",
                null,
                null,
                OffsetDateTime.now().plusDays(1),
                null
        );
        TransactionResponseDTO expectedResponse = TestDtoFactory.createValidTransactionResponseDTO(null, walletId, null);

        when(transactionRepository.findById(id)).thenReturn(Optional.of(transactionEntity));
        when(transactionRepository.save(transactionEntity)).thenReturn(transactionEntity);
        when(mapper.toResponse(transactionEntity)).thenReturn(expectedResponse);


        transactionService.updateTransaction(id, update);


        verify(walletService, never()).incrementWalletBalance(any(), any());
        verify(walletService, never()).decrementWalletBalance(any(), any());
        verify(transactionRepository).save(transactionEntity);
        verify(mapper).updateEntity(update, transactionEntity);
    }

    @Test
    void updateTransaction_WhenTypeChangeButDoesNotBalance_ShouldUpdateWalletBalanceAndReturnsUpdatedTransaction() {
        Long id = 1L;
        Long walletId = 10L;

        CategoryEntity categoryEntity = TestEntityFactory.createValidCategory();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(null);
        walletEntity.setId(walletId);
        TransactionEntity transactionEntity = TestEntityFactory.createValidTransaction(categoryEntity, walletEntity, null);
        transactionEntity.setType(TransactionType.EXPENSE);
        TransactionUpdateDTO update = new TransactionUpdateDTO(
                "New title",
                "New description",
                null,
                TransactionType.INCOME,
                OffsetDateTime.now().plusDays(1),
                null
        );
        TransactionResponseDTO expectedResponse = TestDtoFactory.createValidTransactionResponseDTO(null, walletId, null);

        when(transactionRepository.findById(id)).thenReturn(Optional.of(transactionEntity));
        when(transactionRepository.save(transactionEntity)).thenReturn(transactionEntity);
        when(mapper.toResponse(transactionEntity)).thenReturn(expectedResponse);


        transactionService.updateTransaction(id, update);


        verify(walletService, times(2)).incrementWalletBalance(walletId, transactionEntity.getBalanceInCents());
        verify(transactionRepository).save(transactionEntity);
        verify(mapper).updateEntity(update, transactionEntity);
    }

    @Test
    void updateTransaction_WhenCategoryDoesNotExist_ShouldThrowException() {
        Long transactionId = 1L;
        Long categoryId = 10L;

        TransactionUpdateDTO update = TestDtoFactory.createValidTransactionUpdateDTO(categoryId);
        TransactionEntity entity = TestEntityFactory.createValidTransaction(null, null, null);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(entity));

        doThrow(ResourceNotFoundException.class).when(categoryService).verifyCategoryExists(categoryId);

        assertThrows(ResourceNotFoundException.class, () -> transactionService.updateTransaction(transactionId, update));

        verify(walletService, never()).incrementWalletBalance(any(), any());
        verify(walletService, never()).decrementWalletBalance(any(), any());
        verify(categoryService).verifyCategoryExists(categoryId);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void updateTransaction_WhenTransactionDoesNotExist_ShouldThrowException() {
        Long transactionId = 1L;
        Long categoryId = 10L;

        TransactionUpdateDTO update = TestDtoFactory.createValidTransactionUpdateDTO(categoryId);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.updateTransaction(transactionId, update));

        verify(walletService, never()).incrementWalletBalance(any(), any());
        verify(walletService, never()).decrementWalletBalance(any(), any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void deleteTransaction_WhenTransactionExists_ShouldUpdateWalletAndDeleteTheTransaction() {
        Long transactionId = 1L;
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(null);
        TransactionEntity transactionEntity = TestEntityFactory.createValidTransaction(null, walletEntity, null);
        transactionEntity.setType(TransactionType.EXPENSE);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transactionEntity));

        transactionService.deleteTransaction(transactionId);

        verify(walletService).incrementWalletBalance(transactionEntity.getWallet().getId(), transactionEntity.getBalanceInCents());
        verify(transactionRepository).delete(transactionEntity);

    }

    @Test
    void deleteTransaction_WhenTransactionDoesNotExist_ShouldThrowException() {

        Long transactionId = 1L;
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(null);
        TransactionEntity entity = TestEntityFactory.createValidTransaction(null, walletEntity, null);
        entity.setType(TransactionType.EXPENSE);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.deleteTransaction(transactionId));

        verify(transactionRepository).findById(transactionId);
        verify(walletService,never()).incrementWalletBalance(entity.getWallet().getId(), walletEntity.getBalanceInCents());
        verify(transactionRepository,never()).delete(entity);

    }
}