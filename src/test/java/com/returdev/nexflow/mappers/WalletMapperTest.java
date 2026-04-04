package com.returdev.nexflow.mappers;

import com.returdev.nexflow.dto.request.WalletRequestDTO;
import com.returdev.nexflow.dto.request.update.WalletUpdateDTO;
import com.returdev.nexflow.dto.response.WalletResponseDTO;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.entities.WalletEntity;
import com.returdev.nexflow.repositories.UserRepository;
import com.returdev.nexflow.repositories.WalletRepository;
import com.returdev.nexflow.utils.TestDtoFactory;
import com.returdev.nexflow.utils.TestEntityFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletMapperTest {

    @Mock
    private UserRepository userRepository;
    private WalletMapper mapper;

    @BeforeEach
    void setUp() {

        mapper = new WalletMapper(userRepository);

    }

    @Test
    void toEntity_ShouldMapAllFields() {

        UUID userId = UUID.randomUUID();
        UserEntity mockedUser = UserEntity.builder().id(userId).build();

        when(userRepository.getReferenceById(userId)).thenReturn(mockedUser);

        WalletRequestDTO request = TestDtoFactory.createValidWalletRequestDTO(userId);

        WalletEntity entity = mapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo(request.name());
        assertThat(entity.getCurrencyCode()).isEqualTo(request.currencyCode());
        assertThat(entity.getOverdraftLimit()).isEqualTo(request.overdraftLimit());
        assertThat(entity.getUser())
                .extracting(UserEntity::getId)
                .isEqualTo(userId);

    }

    @Test
    void toResponse_ShouldMapAllFields() {

        WalletEntity entity = TestEntityFactory.createValidWallet(null);

        WalletResponseDTO response = mapper.toResponse(entity);

        assertThat(response.id()).isEqualTo(entity.getId());
        assertThat(response.name()).isEqualTo(entity.getName());
        assertThat(response.balanceInCents()).isEqualTo(entity.getBalanceInCents());
        assertThat(response.currencyCode()).isEqualTo(entity.getCurrencyCode());
        assertThat(response.overdraftLimit()).isEqualTo(entity.getOverdraftLimit());
        assertThat(response.createdAt()).isEqualTo(entity.getCreatedAt());
        assertThat(response.updatedAt()).isEqualTo(entity.getUpdatedAt());

    }

    @Test
    void updateEntity_WithAllFields_UpdatesCorrectFields() {

        WalletEntity entity = TestEntityFactory.createValidWallet(null);

        WalletUpdateDTO dto = TestDtoFactory.createValidWalletUpdateDTO();

        mapper.updateEntity(dto, entity);

        assertThat(entity.getName()).isEqualTo(dto.name());
        assertThat(entity.getCurrencyCode()).isEqualTo(dto.currencyCode());
        assertThat(entity.getOverdraftLimit()).isEqualTo(dto.overdraftLimit());

    }

    @Test
    void updateEntity_WithNullFields_DoesNotOverride() {
        String originalName = "Wallet";
        String originalCurrency = "EUR";
        Long originalLimit = 0L;

        WalletEntity entity = WalletEntity.builder()
                .name(originalName)
                .currencyCode(originalCurrency)
                .overdraftLimit(originalLimit)
                .build();

        WalletUpdateDTO dto = new WalletUpdateDTO(null, null, null);

        mapper.updateEntity(dto, entity);

        assertThat(entity.getName()).isEqualTo(originalName);
        assertThat(entity.getCurrencyCode()).isEqualTo(originalCurrency);
        assertThat(entity.getOverdraftLimit()).isEqualTo(originalLimit);
    }
}