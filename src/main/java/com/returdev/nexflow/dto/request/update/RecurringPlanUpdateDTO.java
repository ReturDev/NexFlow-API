package com.returdev.nexflow.dto.request.update;

import com.returdev.nexflow.model.enums.Frequency;
import com.returdev.nexflow.model.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

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
@Schema(title = "Recurring Plan Update", description = "Schema for updating the details of a recurring financial plan.")
public record RecurringPlanUpdateDTO(
        @Size(min = 1, max = 50, message = "{validation.size.message}")
        @Schema(
                example = "Gym Membership",
                description = "The title or display name of the recurring plan.",
                minLength = 1,
                maxLength = 50
        )
        String title,

        @Size(max = 200, message = "{validation.max_size.message}")
        @Schema(
                example = "Monthly gym payment for premium tier",
                description = "Detailed information about the purpose of the plan.",
                maxLength = 200
        )
        String description,

        @Min(value = 1, message = "{validation.min_value.message}")
        @Schema(
                example = "3000",
                description = "The amount represented in cents (e.g., 30.00€ is represented as 3000).",
                minimum = "1"
        )
        Long balanceInCents,

        @Schema(
                example = "EXPENSE",
                description = "Defines whether the recurring plan represents an income or an expense."
        )
        TransactionType type,

        @Schema(
                example = "2026-04-15T12:00:00Z",
                description = "The initial start date and time of the first plan execution in ISO 8601 format."
        )
        OffsetDateTime startDate,

        @Schema(
                example = "MONTHLY",
                implementation = Frequency.class,
                description = "The frequency at which the plan should be executed."
        )
        Frequency frequency,

        @Min(value = 1, message = "{validation.min_value.message}")
        @Schema(
                example = "1",
                description = "The interval of the specified frequency (e.g., a frequency of MONTHLY and interval of 2 means every 2 months).",
                minimum = "1"
        )
        Integer interval,

        @Schema(
                example = "2026-12-31T23:59:59Z",
                description = "The optional end date and time when the plan stops executing in ISO 8601 format."
        )
        OffsetDateTime endDate,

        @Schema(
                example = "24",
                description = "The unique identifier of the category associated with this plan."
        )
        Long categoryId
) {
}
