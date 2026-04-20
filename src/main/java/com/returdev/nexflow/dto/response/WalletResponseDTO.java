package com.returdev.nexflow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a wallet's current state and balance information.
 * <p>
 * This record is used to return wallet details to the client, providing a read-only
 * view of the account's liquidity and configuration.
 *
 * @param id             the unique identifier for the wallet.
 * @param name           the display name of the wallet (e.g., "Personal Savings").
 * @param balanceInCents the current available balance, represented in cents to
 *                       avoid rounding errors.
 * @param currencyCode   the 3-letter ISO 4217 currency code (e.g., "USD", "EUR").
 * @param overdraftLimit the maximum negative balance allowed for this wallet,
 *                       expressed in cents.
 * @param createdAt      the timestamp indicating when the wallet was opened.
 * @param updatedAt      the timestamp of the most recent modification to the wallet.
 */
@Schema(title = "Wallet Response", description = "Current state and configuration of a user's wallet.")
public record WalletResponseDTO(
        @Schema(example = "1", description = "Unique identifier of the wallet.")
        Long id,

        @Schema(example = "Main Savings", description = "Name of the wallet.")
        String name,

        @Schema(example = "125050", description = "Current calculated balance in cents (e.g., 1250.50€).")
        Long balanceInCents,

        @Schema(example = "EUR", description = "3-letter ISO 4217 currency code.")
        String currencyCode,

        @Schema(example = "0", description = "Allowed negative balance limit in cents.")
        Long overdraftLimit,

        @Schema(example = "2026-01-10T15:00:00")
        LocalDateTime createdAt,

        @Schema(example = "2026-04-15T12:00:00")
        LocalDateTime updatedAt
) {
}
