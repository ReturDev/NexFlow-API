package com.returdev.nexflow.dto.response;

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
public record WalletResponseDTO(
        Long id,
        String name,
        Long balanceInCents,
        String currencyCode,
        Long overdraftLimit,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
