package com.returdev.nexflow.dto.request.update;

import com.returdev.nexflow.model.enums.Frequency;
import com.returdev.nexflow.model.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * DTO for updating the parameters of a Recurring Plan.
 *
 * @param title          updated title for the plan.
 * @param description    updated detailed description.
 * @param balanceInCents updated amount in cents (must be non-negative).
 * @param type           updated {@link TransactionType}.
 * @param startDate      updated start date for the cycle.
 * @param frequency      updated {@link Frequency} unit.
 * @param interval       updated multiplier for frequency (minimum 1).
 * @param endDate        updated (or added) expiration date for the plan.
 * @param categoryId     updated category association ID.
 */
public record RecurringPlanUpdateDTO(
        @Size(min = 1, max = 50, message = "{validation.size.message}")
        String title,
        @Size(max = 200, message = "{validation.max_size.message}")
        String description,
        @Min(value = 1, message = "{validation.min_value.message}")
        Long balanceInCents,
        TransactionType type,
        OffsetDateTime startDate,
        Frequency frequency,
        @Min(value = 1, message = "{validation.min_value.message}")
        Integer interval,
        OffsetDateTime endDate,
        Long categoryId
) {
}
