package com.returdev.nexflow.dto.response;

import com.returdev.nexflow.model.enums.TransactionStatus;
import com.returdev.nexflow.model.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(title = "Transaction Response", description = "Detailed information about a completed or pending transaction.")
public record TransactionResponseDTO(
        @Schema(example = "5001", description = "Unique identifier of the transaction.")
        Long id,

        @Schema(example = "Supermarket", description = "The title of the transaction.")
        String title,

        @Schema(example = "Weekly grocery shopping", description = "Additional transaction notes.")
        String description,

        @Schema(example = "5240", description = "Transaction amount in cents.")
        Long balanceInCents,

        @Schema(example = "EXPENSE", implementation = TransactionType.class)
        TransactionType type,

        @Schema(example = "2026-04-15T18:30:00", description = "Date when the transaction was executed.")
        LocalDateTime date,

        @Schema(example = "COMPLETED", implementation = TransactionStatus.class, description = "Status of the transaction (e.g., COMPLETED, PENDING, CANCELLED).")
        TransactionStatus status,

        @Schema(description = "The category details.")
        CategoryResponseDTO category,

        @Schema(example = "1", description = "The wallet ID involved in this transaction.")
        Long walletId,

        @Schema(example = "101", description = "The ID of the recurring plan that generated this transaction (null if manual).")
        Long planId,

        @Schema(example = "2026-04-15T18:31:00")
        LocalDateTime createdAt,

        @Schema(example = "2026-04-15T18:31:00")
        LocalDateTime updatedAt
) {
}
