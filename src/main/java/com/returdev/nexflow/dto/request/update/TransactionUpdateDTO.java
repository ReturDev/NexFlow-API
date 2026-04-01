package com.returdev.nexflow.dto.request.update;

import com.returdev.nexflow.model.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

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
public record TransactionUpdateDTO(
        @Size(min = 1, max = 50, message = "{validation.size.message}")
        String title,
        @Size(max = 200, message = "{validation.max_size.message}")
        String description,
        @Min(value = 0, message = "{validation.min_value.message}")
        Long balanceInCents,
        TransactionType type,
        LocalDateTime date,
        Long categoryId
) {
}
