package com.returdev.nexflow.dto.request;

import com.returdev.nexflow.model.enums.Frequency;
import com.returdev.nexflow.model.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

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
@Schema(title = "Recurring Plan Request", description = "Schema for setting up a new automated recurring financial plan.")
public record RecurringPlanRequestDTO(
        @Size(max = 50, message = "{validation.max_size.message}")
        @NotBlank(message = "{validation.not_blank.message}")
        @Schema(example = "Netflix Subscription", description = "Short title for the recurring plan.", maxLength = 50)
        String title,

        @Size(max = 200, message = "{validation.max_size.message}")
        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "Monthly standard plan subscription", description = "Detailed description of the plan purpose.", maxLength = 200)
        String description,

        @Min(value = 1, message = "{validation.min_value.message}")
        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "1299", description = "Amount in cents (e.g., 12.99€ is 1299).", minimum = "1")
        Long balanceInCents,

        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "EXPENSE", implementation = TransactionType.class, description = "Specifies if this plan adds or subtracts funds.")
        TransactionType type,

        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "2026-05-01T08:00:00Z", description = "When the first transaction of the plan will be executed.")
        OffsetDateTime startDate,

        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "MONTHLY", implementation = Frequency.class, description = "The recurrence frequency unit.")
        Frequency frequency,

        @Min(value = 1, message = "{validation.min_value.message}")
        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "1", description = "Interval for the frequency (e.g., frequency MONTHLY and interval 3 means every 3 months).", minimum = "1")
        Integer interval,

        @Schema(example = "2027-05-01T08:00:00Z", description = "Optional date to end the recurring plan. If null, it continues indefinitely.")
        OffsetDateTime endDate,

        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "5", description = "ID of the associated category.")
        Long categoryId,

        @NotNull(message = "{validation.not_null.message}")
        @Schema(example = "1", description = "ID of the wallet where the transactions will be applied.")
        Long walletId
) {
}
