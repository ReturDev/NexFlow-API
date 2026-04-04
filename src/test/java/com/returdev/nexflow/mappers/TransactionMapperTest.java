package com.returdev.nexflow.mappers;

import com.returdev.nexflow.dto.request.TransactionRequestDTO;
import com.returdev.nexflow.dto.request.update.TransactionUpdateDTO;
import com.returdev.nexflow.dto.response.CategoryResponseDTO;
import com.returdev.nexflow.dto.response.TransactionResponseDTO;
import com.returdev.nexflow.model.entities.CategoryEntity;
import com.returdev.nexflow.model.entities.TransactionEntity;
import com.returdev.nexflow.model.entities.WalletEntity;
import com.returdev.nexflow.model.enums.TransactionStatus;
import com.returdev.nexflow.model.enums.TransactionType;
import com.returdev.nexflow.repositories.CategoryRepository;
import com.returdev.nexflow.repositories.WalletRepository;
import com.returdev.nexflow.utils.TestDtoFactory;
import com.returdev.nexflow.utils.TestEntityFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.google.common.math.LongMath;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionMapperTest {

    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private WalletRepository walletRepository;

    private TransactionMapper mapper;


    @BeforeEach
    void setUp() {

        mapper = new TransactionMapper(
                categoryMapper,
                categoryRepository,
                walletRepository
        );

    }

    @Test
    void toEntity_ShouldMapAllFields() {

        Long categoryId = 1L;
        Long walletId = 3L;

        CategoryEntity categoryEntity = CategoryEntity.builder().id(categoryId).build();
        WalletEntity walletEntity = WalletEntity.builder().id(walletId).build();

        when(categoryRepository.getReferenceById(categoryId)).thenReturn(categoryEntity);
        when(walletRepository.getReferenceById(walletId)).thenReturn(walletEntity);


        TransactionRequestDTO request = TestDtoFactory.createValidTransactionRequestDTO(categoryId,walletId);

        TransactionEntity entity = mapper.toEntity(request);

        assertThat(entity.getTitle()).isEqualTo(request.title());
        assertThat(entity.getDescription()).isEqualTo(request.description());
        assertThat(entity.getBalanceInCents()).isEqualTo(request.balanceInCents());
        assertThat(entity.getType()).isEqualTo(request.type());
        assertThat(entity.getDate()).isEqualTo(mapper.normalizeDateToUTC(request.date()));
        assertThat(entity.getCategory().getId()).isEqualTo(request.categoryId());
        assertThat(entity.getWallet().getId()).isEqualTo(request.walletId());

    }

    @Test
    void toResponse_ShouldMapAllFields() {

        CategoryEntity categoryEntity = new CategoryEntity();
        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO(null, null, null, null, null);
        when(categoryMapper.toResponse(categoryEntity)).thenReturn(categoryResponseDTO);

        WalletEntity walletEntity = TestEntityFactory.createValidWallet(null);

        TransactionEntity entity = TestEntityFactory.createValidTransaction(categoryEntity, walletEntity, null);

        TransactionResponseDTO response = mapper.toResponse(entity);

        assertThat(response.id()).isEqualTo(entity.getId());
        assertThat(response.title()).isEqualTo(entity.getTitle());
        assertThat(response.description()).isEqualTo(entity.getDescription());
        assertThat(response.balanceInCents()).isEqualTo(entity.getBalanceInCents());
        assertThat(response.type()).isEqualTo(entity.getType());
        assertThat(response.date()).isEqualTo(entity.getDate());
        assertThat(response.status()).isEqualTo(entity.getStatus());
        assertThat(response.category().id()).isEqualTo(entity.getCategory().getId());
        assertThat(response.walletId()).isEqualTo(entity.getWallet().getId());
        assertThat(response.planId()).isNull();
        assertThat(response.createdAt()).isEqualTo(entity.getCreatedAt());
        assertThat(response.updatedAt()).isEqualTo(entity.getUpdatedAt());

    }

    @Test
    void updateEntity_WithAllFields_UpdatesCorrectFields() {


        CategoryEntity originalCategory = TestEntityFactory.createValidCategory();
        Long newCategoryId = 2L;
        CategoryEntity newCategory = CategoryEntity.builder().id(newCategoryId).build();
        when(categoryRepository.getReferenceById(newCategoryId)).thenReturn(newCategory);

        TransactionEntity entity = TestEntityFactory.createValidTransaction(originalCategory, null, null);

        TransactionUpdateDTO dto = TestDtoFactory.createValidTransactionUpdateDTO(newCategoryId);

        mapper.updateEntity(dto, entity);

        assertThat(entity.getTitle()).isEqualTo(dto.title());
        assertThat(entity.getDescription()).isEqualTo(dto.description());
        assertThat(entity.getBalanceInCents()).isEqualTo(dto.balanceInCents());
        assertThat(entity.getType()).isEqualTo(dto.type());
        assertThat(entity.getDate()).isEqualTo(mapper.normalizeDateToUTC(dto.date()));
        assertThat(entity.getCategory().getId()).isEqualTo(dto.categoryId());

    }
    @Test
    void updateEntity_WithNullFields_DoesNotOverride() {

        String originalTitle = "Title";
        String originalDescription = "description";
        Long originalBalance = 30L;
        TransactionType originalType = TransactionType.EXPENSE;
        LocalDateTime originalDate = LocalDateTime.now();
        CategoryEntity originalCategory = TestEntityFactory.createValidCategory();

        TransactionEntity entity = TransactionEntity.builder()
                .title(originalTitle)
                .description(originalDescription)
                .balanceInCents(originalBalance)
                .type(originalType)
                .date(originalDate)
                .category(originalCategory)
                .build();

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                null,
                null,
                null,
                null,
                null,
                null
        );

        mapper.updateEntity(dto, entity);

        assertThat(entity.getTitle()).isEqualTo(originalTitle);
        assertThat(entity.getDescription()).isEqualTo(originalDescription);
        assertThat(entity.getBalanceInCents()).isEqualTo(originalBalance);
        assertThat(entity.getType()).isEqualTo(originalType);
        assertThat(entity.getDate()).isEqualTo(originalDate);
        assertThat(entity.getCategory().getId()).isEqualTo(originalCategory.getId());

    }


}