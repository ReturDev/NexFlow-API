package com.returdev.nexflow.mappers;

import com.returdev.nexflow.dto.request.TransactionRequestDTO;
import com.returdev.nexflow.dto.request.update.TransactionUpdateDTO;
import com.returdev.nexflow.dto.response.TransactionResponseDTO;
import com.returdev.nexflow.model.entities.TransactionEntity;
import com.returdev.nexflow.repositories.CategoryRepository;
import com.returdev.nexflow.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of {@link Mapper} for {@link TransactionEntity}.
 */
@RequiredArgsConstructor
@Component
public class TransactionMapper implements Mapper<TransactionEntity, TransactionResponseDTO, TransactionRequestDTO, TransactionUpdateDTO> {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final WalletRepository walletRepository;

    /**
     * Maps a {@link TransactionRequestDTO} to a {@link TransactionEntity}.
     *
     * @param request the incoming transaction data from the API.
     * @return a {@link TransactionEntity} builder-populated and ready for persistence.
     */
    @Override
    public TransactionEntity toEntity(TransactionRequestDTO request) {
        return TransactionEntity.builder()
                .title(request.title())
                .description(request.description())
                .balanceInCents(request.balanceInCents())
                .type(request.type())
                .date(normalizeDateToUTC(request.date()))
                .category(categoryRepository.getReferenceById(request.categoryId()))
                .wallet(walletRepository.getReferenceById(request.walletId()))
                .build();
    }

    /**
     * Maps a {@link TransactionEntity} to a {@link TransactionResponseDTO}.
     *
     * @param entity the persistence entity representing a transaction.
     * @return a {@link TransactionResponseDTO} formatted for client-side consumption.
     */
    @Override
    public TransactionResponseDTO toResponse(TransactionEntity entity) {
        return new TransactionResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getBalanceInCents(),
                entity.getType(),
                entity.getDate(),
                entity.getStatus(),
                categoryMapper.toResponse(entity.getCategory()),
                entity.getWallet().getId(),
                entity.getPlan() == null ? null : entity.getPlan().getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Partially updates an existing {@link TransactionEntity} using the provided {@link TransactionUpdateDTO}.
     *
     * @param dto    the data transfer object containing the modified transaction details.
     * @param entity the existing transaction entity to be updated.
     */
    @Override
    public void updateEntity(TransactionUpdateDTO dto, TransactionEntity entity) {
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
        if (dto.date() != null) {
            entity.setDate(normalizeDateToUTC(dto.date()));
        }
        if (dto.categoryId() != null) {
            entity.setCategory(categoryRepository.getReferenceById(dto.categoryId()));
        }
    }
}
