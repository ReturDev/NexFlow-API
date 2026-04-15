package com.returdev.nexflow.dto.request;

import com.returdev.nexflow.model.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
@Schema(title = "Transaction Request", description = "Parameters required to create a new manual financial transaction.")
public record TransactionRequestDTO(
        @NotBlank(message = "{validation.not_blank.message}")
        @Size(max = 50, message = "{validation.max_size.message}")
        @Schema(example = "Groceries", description = "A short title for the transaction.", maxLength = 50)
        String title,

        @NotNull(message = "{validation.not_null.message}")
        @Size(max = 200, message = "{validation.max_size.message}")
        @Schema(example = "Weekly shopping at local supermarket", description = "Detailed description or notes about the transaction.", maxLength = 200)
        String description,

        @NotNull(message = "{validation.not_null.message}")
        @Min(value = 1, message = "{validation.min_value.message}")
        @Schema(example = "4550", description = "Amount in cents (e.g., 45.50 is 4550). Must be at least 1.", minimum = "1")
        Long balanceInCents,

        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "EXPENSE", implementation = TransactionType.class, description = "Indicates if this transaction is an income or an expense.")
        TransactionType type,

        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "2026-04-15T15:30:00Z", description = "The exact date and time when the transaction took place (ISO 8601).")
        OffsetDateTime date,

        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "12", description = "The ID of the category associated with this transaction.")
        Long categoryId,

        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "1", description = "The ID of the wallet where this transaction will be recorded.")
        Long walletId
) {
}
