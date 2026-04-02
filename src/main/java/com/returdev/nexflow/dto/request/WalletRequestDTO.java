package com.returdev.nexflow.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;


/**
 * Data Transfer Object for creating or initializing a new Wallet.
 *
 * @param name           the display name for the wallet (max 50 characters).
 *                       Cannot be blank.
 * @param currencyCode   the 3-character ISO 4217 currency code (e.g., "USD", "BRL").
 *                       Must be exactly 3 characters.
 * @param overdraftLimit the maximum allowed negative balance, expressed in cents.
 *                       Must be zero or greater.
 * @param userId         the unique identifier (UUID) of the {@code User} who
 *                       will own this wallet. Required for account association.
 */
public record WalletRequestDTO(
        @NotBlank(message = "{validation.not_blank.message}")
        @Size(max = 50, message = "{validation.max_size.message}")
        String name,
        @Size(min = 3, max = 3, message = "{validation.fix_size.message}")
        @NotBlank(message = "{validation.not_blank.message}")
        String currencyCode,
        @Min(value = 0, message = "{validation.min_value.message}")
        @NotNull(message = "{validation.not_null.message}")
        Long overdraftLimit,
        @NotNull(message = "{validation.not_null.message}")
        UUID userId
) {
}
