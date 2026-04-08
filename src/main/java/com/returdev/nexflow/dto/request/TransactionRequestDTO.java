package com.returdev.nexflow.dto.request;

import com.returdev.nexflow.model.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Data Transfer Object for recording a new transaction or updating an existing one.
 * <p>
 * This record captures the core details of a financial movement. It requires
 * explicit links to both a category and a wallet to maintain data integrity.
 *
 * @param title          a brief label for the transaction (max 50 chars).
 * @param description    additional context or notes for the transaction (max 200 chars).
 * @param balanceInCents the absolute monetary value in cents. Must be non-negative.
 * @param type           the {@link TransactionType} (e.g., INCOME, EXPENSE).
 * @param date           the effective date and time of the transaction.
 * @param categoryId     the ID of the category this transaction is filed under.
 * @param walletId       the ID of the wallet to be debited or credited.
 */
public record TransactionRequestDTO(
        @NotBlank(message = "{validation.not_blank.message}")
        @Size(max = 50, message = "{validation.max_size.message}")
        String title,
        @NotNull(message = "{validation.not_null.message}")
        @Size(max = 200, message = "{validation.max_size.message}")
        String description,
        @NotNull(message = "{validation.not_null.message}")
        @Min(value = 1, message = "{validation.min_value.message}")
        Long balanceInCents,
        @NotNull(message = "{validation.not_null.message}")
        TransactionType type,
        @NotNull(message = "{validation.not_null.message}")
        OffsetDateTime date,
        @NotNull(message = "{validation.not_null.message}")
        Long categoryId,
        @NotNull(message = "{validation.not_null.message}")
        Long walletId
) {
}
