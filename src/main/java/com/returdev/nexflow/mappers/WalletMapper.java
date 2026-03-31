package com.returdev.nexflow.mappers;

import com.returdev.nexflow.dto.request.WalletRequestDTO;
import com.returdev.nexflow.dto.response.WalletResponseDTO;
import com.returdev.nexflow.model.entities.WalletEntity;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of {@link Mapper} for {@link WalletEntity}.
 */
@Component
public class WalletMapper implements Mapper<WalletEntity, WalletResponseDTO, WalletRequestDTO> {

    /**
     * Maps a {@link WalletRequestDTO} to a {@link WalletEntity}.
     *
     * @param request the wallet creation/update request.
     * @return a {@link WalletEntity} prepared for persistence.
     */
    @Override
    public WalletEntity toEntity(WalletRequestDTO request) {
        return WalletEntity.builder()
                .name(request.name())
                .currencyCode(request.currencyCode())
                .overdraftLimit(request.overdraftLimit())
                .build();
    }

    /**
     * Maps a {@link WalletEntity} to a {@link WalletResponseDTO}.
     *
     * @param entity the persistence entity.
     * @return a DTO containing the wallet's current state and metadata.
     */
    @Override
    public WalletResponseDTO toResponse(WalletEntity entity) {
        return new WalletResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getBalanceInCents(),
                entity.getCurrencyCode(),
                entity.getOverdraftLimit(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

}
