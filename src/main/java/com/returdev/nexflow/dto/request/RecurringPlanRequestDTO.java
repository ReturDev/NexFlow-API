package com.returdev.nexflow.dto.request;

import com.returdev.nexflow.model.enums.Frequency;
import com.returdev.nexflow.model.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for creating or updating a recurring transaction plan.
 * <p>
 * This record captures the scheduling configuration and monetary details required
 * to automate future transactions. It includes strict validation to ensure
 * logical consistency (e.g., positive intervals and non-negative balances).
 *
 * @param title          a concise name for the plan (max 50 characters).
 * @param description    a detailed explanation of the plan (max 200 characters).
 * @param balanceInCents the transaction amount in cents. Must be zero or greater
 *                       to prevent negative input values.
 * @param type           the {@link TransactionType} indicating if this is income or an expense.
 * @param startDate      the date and time when the recurring plan should begin execution.
 * @param frequency      the unit of time for recurrence (e.g., {@link Frequency#DAILY}).
 * @param interval       the multiplier for the frequency. Must be at least 1
 *                       (e.g., an interval of 1 means "every [frequency]").
 * @param endDate        an optional expiration date for the recurring plan.
 * @param categoryId     the unique identifier of the associated category.
 * @param walletId       the unique identifier of the wallet where transactions will be posted.
 */
public record RecurringPlanRequestDTO(
        @Size(max = 50, message = "{validation.max_size.message}")
        @NotBlank(message = "{validation.not_blank.message}")
        String title,
        @Size(max = 200, message = "{validation.max_size.message}")
        @NotNull(message = "{validation.not_null.message}")
        String description,
        @Min(value = 0, message = "{validation.min_value.message}")
        @NotNull(message = "{validation.not_null.message}")
        Long balanceInCents,
        @NotNull(message = "{validation.not_null.message}")
        TransactionType type,
        @NotNull(message = "{validation.not_null.message}")
        LocalDateTime startDate,
        @NotNull(message = "{validation.not_null.message}")
        Frequency frequency,
        @Min(value = 1, message = "{validation.min_value.message}")
        @NotNull(message = "{validation.not_null.message}")
        Integer interval,
        LocalDateTime endDate,
        @NotNull(message = "{validation.not_null.message}")
        Long categoryId,
        @NotNull(message = "{validation.not_null.message}")
        Long walletId
) {
}
