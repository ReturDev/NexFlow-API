package com.returdev.nexflow.dto.request.update;

import com.returdev.nexflow.model.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

/**
 * DTO for updating a specific Transaction's details.
 *
 * @param title          updated transaction title.
 * @param description    updated transaction notes.
 * @param balanceInCents updated amount in cents.
 * @param type           updated {@link TransactionType}.
 * @param date           updated effective date of the transaction.
 * @param categoryId     updated category association ID.
 */
@Schema(title = "Transaction Update", description = "Schema for modifying an existing financial transaction.")
public record TransactionUpdateDTO(
        @Size(min = 1, max = 50, message = "{validation.size.message}")
        @Schema(
                example = "Groceries Store",
                description = "The title or short name of the transaction.",
                minLength = 1,
                maxLength = 50
        )
        String title,

        @Size(max = 200, message = "{validation.max_size.message}")
        @Schema(
                example = "Weekly grocery shopping at the local market",
                description = "Additional notes or details about the transaction.",
                maxLength = 200
        )
        String description,

        @Min(value = 1, message = "{validation.min_value.message}")
        @Schema(
                example = "4550",
                description = "The transaction amount in cents (e.g., 45.50€ is 4550). Must be greater than 0.",
                minimum = "1"
        )
        Long balanceInCents,

        @Schema(
                example = "EXPENSE",
                implementation = TransactionType.class,
                description = "Indicates if the transaction is an income or an expense."
        )
        TransactionType type,

        @Schema(
                example = "2026-04-15T18:45:00Z",
                description = "The exact date and time the transaction occurred in ISO 8601 format."
        )
        OffsetDateTime date,

        @Schema(
                example = "12",
                description = "The ID of the category this transaction belongs to."
        )
        Long categoryId
) {
}
