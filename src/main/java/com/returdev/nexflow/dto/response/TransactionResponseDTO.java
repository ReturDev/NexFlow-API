package com.returdev.nexflow.dto.response;

import com.returdev.nexflow.model.enums.TransactionStatus;
import com.returdev.nexflow.model.enums.TransactionType;

import java.time.LocalDateTime;

/**
 * Data Transfer Object providing a detailed view of a processed transaction.
 * <p>
 * This record includes system-generated metadata such as the transaction status,
 * audit timestamps, and an optional link to a recurring plan if applicable.
 *
 * @param id             the unique identifier of the transaction.
 * @param title          the transaction title.
 * @param description    the transaction description.
 * @param balanceInCents the transaction amount in cents.
 * @param type           the {@link TransactionType} of the movement.
 * @param date           the date the transaction was recorded as taking place.
 * @param status         the current {@link TransactionStatus} (e.g., COMPLETED, PENDING).
 * @param category       the full {@link CategoryResponseDTO} details for display.
 * @param walletId       the ID of the associated wallet.
 * @param planId         the ID of the originating {@code RecurringPlan}, or {@code null}
 *                       if this was a manual transaction.
 * @param createdAt      the timestamp of record creation.
 * @param updatedAt      the timestamp of the last record update.
 */
public record TransactionResponseDTO(
        Long id,
        String title,
        String description,
        Long balanceInCents,
        TransactionType type,
        LocalDateTime date,
        TransactionStatus status,
        CategoryResponseDTO category,
        Long walletId,
        Long planId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
