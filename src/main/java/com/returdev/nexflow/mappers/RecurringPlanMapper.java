package com.returdev.nexflow.mappers;

import com.returdev.nexflow.dto.request.RecurringPlanRequestDTO;
import com.returdev.nexflow.dto.request.update.RecurringPlanUpdateDTO;
import com.returdev.nexflow.dto.response.RecurringPlanResponseDTO;
import com.returdev.nexflow.model.entities.RecurringPlanEntity;
import com.returdev.nexflow.repositories.CategoryRepository;
import com.returdev.nexflow.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of {@link Mapper} for {@link RecurringPlanEntity}.
 */
@Component
@RequiredArgsConstructor
public class RecurringPlanMapper implements Mapper<RecurringPlanEntity, RecurringPlanResponseDTO, RecurringPlanRequestDTO, RecurringPlanUpdateDTO> {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final WalletRepository walletRepository;

    /**
     * Maps a {@link RecurringPlanRequestDTO} to a {@link RecurringPlanEntity}.
     *
     * @param request the DTO containing plan details and association IDs.
     * @return a {@link RecurringPlanEntity} linked to the specified wallet and category.
     */
    @Override
    public RecurringPlanEntity toEntity(RecurringPlanRequestDTO request) {
        return RecurringPlanEntity.builder()
                .title(request.title())
                .description(request.description())
                .balanceInCents(request.balanceInCents())
                .type(request.type())
                .startDate(normalizeDateToUTC(request.startDate()))
                .frequency(request.frequency())
                .interval(request.interval())
                .endDate(normalizeDateToUTC(request.endDate()))
                .category(categoryRepository.getReferenceById(request.categoryId()))
                .wallet(walletRepository.getReferenceById(request.walletId()))
                .build();
    }

    /**
     * Maps a {@link RecurringPlanEntity} to a {@link RecurringPlanResponseDTO}.
     *
     * @param entity the source recurring plan entity.
     * @return a detailed {@link RecurringPlanResponseDTO} including nested category data.
     */
    @Override
    public RecurringPlanResponseDTO toResponse(RecurringPlanEntity entity) {
        return new RecurringPlanResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getBalanceInCents(),
                entity.getType(),
                entity.getStartDate(),
                entity.getFrequency(),
                entity.getInterval(),
                entity.getNextExecutionDate(),
                entity.getStatus(),
                entity.getEndDate(),
                categoryMapper.toResponse(entity.getCategory()),
                entity.getWallet().getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Partially updates an existing {@link RecurringPlanEntity} based on the provided DTO.
     *
     * @param dto    the DTO containing the updated plan details.
     * @param entity the existing persistence entity to be modified.
     */
    @Override
    public void updateEntity(RecurringPlanUpdateDTO dto, RecurringPlanEntity entity) {
        if (dto.title() != null) {
            entity.setTitle(dto.title());
        }
        if (dto.description() != null) {
            entity.setDescription(dto.description());
        }
        if (dto.balanceInCents() != null) {
            entity.setBalanceInCents(dto.balanceInCents());
        }
        if (dto.type() != null) {
            entity.setType(dto.type());
        }
        if (dto.startDate() != null) {
            entity.setStartDate(normalizeDateToUTC(dto.startDate()));
        }
        if (dto.frequency() != null) {
            entity.setFrequency(dto.frequency());
        }
        if (dto.interval() != null) {
            entity.setInterval(dto.interval());
        }
        if (dto.endDate() != null) {
            entity.setEndDate(normalizeDateToUTC(dto.endDate()));
        }
        if (dto.categoryId() != null) {
            entity.setCategory(categoryRepository.getReferenceById(dto.categoryId()));
        }
    }
}
