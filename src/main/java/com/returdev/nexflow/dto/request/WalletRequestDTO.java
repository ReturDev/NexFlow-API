package com.returdev.nexflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(title = "Wallet Creation Request", description = "Parameters needed to initialize a new financial wallet.")
public record WalletRequestDTO(
        @NotBlank(message = "{validation.not_blank.message}")
        @Size(max = 50, message = "{validation.max_size.message}")
        @Schema(example = "Cash Wallet", description = "Friendly name for the wallet.", maxLength = 50)
        String name,

        @Size(min = 3, max = 3, message = "{validation.fix_size.message}")
        @NotBlank(message = "{validation.not_blank.message}")
        @Schema(example = "EUR", description = "The 3-letter ISO 4217 currency code.", minLength = 3, maxLength = 3)
        String currencyCode,

        @Min(value = 0, message = "{validation.min_value.message}")
        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "10000", description = "Maximum negative balance allowed in cents (e.g., 100.00 is 10000).", minimum = "0")
        Long overdraftLimit,

        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000", description = "The unique identifier (UUID) of the user who owns this wallet.")
        UUID userId
) {
}
