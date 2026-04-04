package com.returdev.nexflow.mappers;

import com.returdev.nexflow.dto.request.WalletRequestDTO;
import com.returdev.nexflow.dto.request.update.WalletUpdateDTO;
import com.returdev.nexflow.dto.response.WalletResponseDTO;
import com.returdev.nexflow.model.entities.WalletEntity;
import com.returdev.nexflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of {@link Mapper} for {@link WalletEntity}.
 */
@Component
@RequiredArgsConstructor
public class WalletMapper implements Mapper<WalletEntity, WalletResponseDTO, WalletRequestDTO, WalletUpdateDTO> {

    private final UserRepository repository;

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
                .user(repository.getReferenceById(request.userId()))
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

    /**
     * Partially updates an existing {@link WalletEntity} with values from a {@link WalletUpdateDTO}.
     *
     * @param dto    the DTO containing the updated wallet settings.
     * @param entity the existing wallet entity to be modified.
     */
    @Override
    public void updateEntity(WalletUpdateDTO dto, WalletEntity entity) {
        if (dto.name() != null) {
            entity.setName(dto.name());
        }
        if (dto.currencyCode() != null) {
            entity.setCurrencyCode(dto.currencyCode());
        }
        if (dto.overdraftLimit() != null) {
            entity.setOverdraftLimit(dto.overdraftLimit());
        }
    }

}
