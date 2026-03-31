package com.returdev.nexflow.mappers;

import com.returdev.nexflow.dto.request.TransactionRequestDTO;
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
public class TransactionMapper implements Mapper<TransactionEntity, TransactionResponseDTO, TransactionRequestDTO>{

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
                .date(request.date())
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
                entity.getPlan().getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
