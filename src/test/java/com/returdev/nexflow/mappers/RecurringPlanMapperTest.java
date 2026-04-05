package com.returdev.nexflow.mappers;

import com.returdev.nexflow.dto.request.RecurringPlanRequestDTO;
import com.returdev.nexflow.dto.request.update.RecurringPlanUpdateDTO;
import com.returdev.nexflow.dto.response.CategoryResponseDTO;
import com.returdev.nexflow.dto.response.RecurringPlanResponseDTO;
import com.returdev.nexflow.model.entities.CategoryEntity;
import com.returdev.nexflow.model.entities.RecurringPlanEntity;
import com.returdev.nexflow.model.entities.TransactionEntity;
import com.returdev.nexflow.model.entities.WalletEntity;
import com.returdev.nexflow.model.enums.Frequency;
import com.returdev.nexflow.model.enums.TransactionStatus;
import com.returdev.nexflow.model.enums.TransactionType;
import com.returdev.nexflow.repositories.CategoryRepository;
import com.returdev.nexflow.repositories.WalletRepository;
import com.returdev.nexflow.utils.TestDtoFactory;
import com.returdev.nexflow.utils.TestEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecurringPlanMapperTest {

    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private WalletRepository walletRepository;

    private RecurringPlanMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RecurringPlanMapper(categoryMapper, categoryRepository, walletRepository);
    }

    @Test
    void toEntity_ShouldMapAllFields() {

        Long categoryId = 1L;
        Long walletId = 3L;

        CategoryEntity categoryEntity = CategoryEntity.builder().id(categoryId).build();
        WalletEntity walletEntity = WalletEntity.builder().id(walletId).build();

        when(categoryRepository.getReferenceById(categoryId)).thenReturn(categoryEntity);
        when(walletRepository.getReferenceById(walletId)).thenReturn(walletEntity);

        RecurringPlanRequestDTO request = TestDtoFactory.createValidPlanRequestDTO(categoryId, walletId);

        RecurringPlanEntity entity = mapper.toEntity(request);

        assertThat(entity.getTitle()).isEqualTo(request.title());
        assertThat(entity.getDescription()).isEqualTo(request.description());
        assertThat(entity.getBalanceInCents()).isEqualTo(request.balanceInCents());
        assertThat(entity.getType()).isEqualTo(request.type());
        assertThat(entity.getStartDate()).isEqualTo(mapper.normalizeDateToUTC(request.startDate()));
        assertThat(entity.getFrequency()).isEqualTo(request.frequency());
        assertThat(entity.getInterval()).isEqualTo(request.interval());
        assertThat(entity.getEndDate()).isEqualTo(mapper.normalizeDateToUTC(request.endDate()));
        assertThat(entity.getCategory().getId()).isEqualTo(request.categoryId());
        assertThat(entity.getWallet().getId()).isEqualTo(request.walletId());

    }

    @Test
    void toResponse_ShouldMapAllFields() {

        CategoryEntity categoryEntity = new CategoryEntity();
        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO(null, null, null, null, null);
        when(categoryMapper.toResponse(categoryEntity)).thenReturn(categoryResponseDTO);

        Long walletId = 2L;
        WalletEntity walletEntity = WalletEntity.builder().id(walletId).build();

        RecurringPlanEntity entity = TestEntityFactory.createValidRecurringPlan(categoryEntity, walletEntity);

        RecurringPlanResponseDTO response = mapper.toResponse(entity);

        assertThat(response.id()).isEqualTo(entity.getId());
        assertThat(response.title()).isEqualTo(entity.getTitle());
        assertThat(response.description()).isEqualTo(entity.getDescription());
        assertThat(response.balanceInCents()).isEqualTo(entity.getBalanceInCents());
        assertThat(response.type()).isEqualTo(entity.getType());
        assertThat(response.startDate()).isEqualTo(entity.getStartDate());
        assertThat(response.frequency()).isEqualTo(entity.getFrequency());
        assertThat(response.interval()).isEqualTo(entity.getInterval());
        assertThat(response.nextExecutionDate()).isEqualTo(entity.getNextExecutionDate());
        assertThat(response.status()).isEqualTo(entity.getStatus());
        assertThat(response.endDate()).isEqualTo(entity.getEndDate());
        assertThat(response.category().id()).isEqualTo(entity.getCategory().getId());
        assertThat(response.walletId()).isEqualTo(entity.getWallet().getId());
        assertThat(response.createdAt()).isEqualTo(entity.getCreatedAt());
        assertThat(response.updatedAt()).isEqualTo(entity.getUpdatedAt());

    }

    @Test
    void updateEntity_WithAllFields_ShouldMapCorrectFields() {

        CategoryEntity categoryEntity = TestEntityFactory.createValidCategory();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(null);

        Long newCategoryId = 2L;
        CategoryEntity newCategoryEntity = CategoryEntity.builder().id(newCategoryId).build();
        when(categoryRepository.getReferenceById(newCategoryId)).thenReturn(newCategoryEntity);

        RecurringPlanEntity entity = TestEntityFactory.createValidRecurringPlan(categoryEntity, walletEntity);

        RecurringPlanUpdateDTO dto = TestDtoFactory.createValidPlanUpdateDTO(newCategoryId);

        mapper.updateEntity(dto, entity);

        assertThat(entity.getTitle()).isEqualTo(dto.title());
        assertThat(entity.getDescription()).isEqualTo(dto.description());
        assertThat(entity.getBalanceInCents()).isEqualTo(dto.balanceInCents());
        assertThat(entity.getType()).isEqualTo(dto.type());
        assertThat(entity.getStartDate()).isEqualTo(mapper.normalizeDateToUTC(dto.startDate()));
        assertThat(entity.getFrequency()).isEqualTo(dto.frequency());
        assertThat(entity.getInterval()).isEqualTo(dto.interval());
        assertThat(entity.getEndDate()).isEqualTo(mapper.normalizeDateToUTC(dto.endDate()));
        assertThat(entity.getCategory().getId()).isEqualTo(dto.categoryId());

    }

    @Test
    void updateEntity_WithAllFields_DoesNotOverride() {

        String originalTitle = "Title";
        String originalDescription = "description";
        Long originalBalance = 30L;
        TransactionType originalType = TransactionType.EXPENSE;
        LocalDateTime originalDate = LocalDateTime.now();
        Frequency originalFrequency = Frequency.DAILY;
        Integer originalInterval = 1;
        CategoryEntity originalCategory = TestEntityFactory.createValidCategory();


        RecurringPlanEntity entity = RecurringPlanEntity.builder()
                .title(originalTitle)
                .description(originalDescription)
                .balanceInCents(originalBalance)
                .type(originalType)
                .startDate(originalDate)
                .frequency(originalFrequency)
                .interval(originalInterval)
                .endDate(originalDate)
                .category(originalCategory)
                .build();

        RecurringPlanUpdateDTO dto = new RecurringPlanUpdateDTO(
                null,
                null,
                null,
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
        assertThat(entity.getStartDate()).isEqualTo(originalDate);
        assertThat(entity.getFrequency()).isEqualTo(originalFrequency);
        assertThat(entity.getInterval()).isEqualTo(originalInterval);
        assertThat(entity.getEndDate()).isEqualTo(originalDate);
        assertThat(entity.getCategory().getId()).isEqualTo(originalCategory.getId());

    }

    @Test
    void toTransactionEntity_WithAllFields_ShouldMapCorrectFields() {

        CategoryEntity categoryEntity = TestEntityFactory.createValidCategory();
        WalletEntity walletEntity = TestEntityFactory.createValidWallet(null);

        RecurringPlanEntity planEntity = TestEntityFactory.createValidRecurringPlan(categoryEntity,walletEntity);

        TransactionEntity transactionEntity = mapper.toTransactionEntity(planEntity);

        assertThat(transactionEntity.getTitle()).isEqualTo(planEntity.getTitle());
        assertThat(transactionEntity.getDescription()).isEqualTo(planEntity.getDescription());
        assertThat(transactionEntity.getBalanceInCents()).isEqualTo(planEntity.getBalanceInCents());
        assertThat(transactionEntity.getType()).isEqualTo(planEntity.getType());
        assertThat(transactionEntity.getDate()).isEqualTo(planEntity.getNextExecutionDate());
        assertThat(transactionEntity.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(transactionEntity.getCategory()).isEqualTo(planEntity.getCategory());
        assertThat(transactionEntity.getWallet()).isEqualTo(planEntity.getWallet());
        assertThat(transactionEntity.getPlan()).isEqualTo(planEntity);


    }

}