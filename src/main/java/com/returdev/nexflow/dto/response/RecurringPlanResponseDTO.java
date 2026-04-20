package com.returdev.nexflow.dto.response;

import com.returdev.nexflow.model.enums.Frequency;
import com.returdev.nexflow.model.enums.PlanStatus;
import com.returdev.nexflow.model.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a comprehensive view of a recurring financial plan.
 * <p>
 * This record details the scheduling logic, monetary values, and status of automated
 * transactions, including nested category information and audit timestamps.
 *
 * @param id                the unique identifier for the recurring plan.
 * @param title             a short, descriptive name for the plan (e.g., "Monthly Rent").
 * @param description       an optional detailed explanation of the plan's purpose.
 * @param balanceInCents    the monetary amount of the transaction, stored in cents
 *                          to avoid floating-point precision issues.
 * @param type              the {@link TransactionType} (e.g., INCOME or EXPENSE).
 * @param startDate         the date and time when the recurring series begins.
 * @param frequency         the {@link Frequency} unit (e.g., DAILY, WEEKLY, MONTHLY).
 * @param interval          the multiplier for the frequency (e.g., an interval of 2
 *                          with WEEKLY frequency means "every two weeks").
 * @param nextExecutionDate the calculated timestamp for when the next transaction
 *                          is scheduled to occur.
 * @param status          the current operational status.
 * @param endDate           the optional expiration date for the recurring series.
 * @param category          the nested {@link CategoryResponseDTO} associated with this plan.
 * @param walletId          the unique identifier of the wallet this plan belongs to.
 * @param createdAt         the timestamp indicating when this plan was created.
 * @param updatedAt         the timestamp indicating the last modification to the plan.
 */
@Schema(title = "Recurring Plan Response", description = "Detailed information about a recurring financial schedule.")
public record RecurringPlanResponseDTO(
        @Schema(example = "101", description = "Unique identifier of the recurring plan.")
        Long id,

        @Schema(example = "Netflix Subscription", description = "The title of the plan.")
        String title,

        @Schema(example = "Premium monthly plan", description = "A detailed description of the plan.")
        String description,

        @Schema(example = "1799", description = "Amount in cents (e.g., 17.99€ is 1799).")
        Long balanceInCents,

        @Schema(example = "EXPENSE", implementation = TransactionType.class, description = "Indicates if the plan adds or subtracts funds.")
        TransactionType type,

        @Schema(example = "2026-01-01T10:00:00", description = "Date when the plan was first activated.")
        LocalDateTime startDate,

        @Schema(example = "MONTHLY", implementation = Frequency.class, description = "Frequency of execution.")
        Frequency frequency,

        @Schema(example = "1", description = "The recurrence interval.")
        Integer interval,

        @Schema(example = "2026-05-01T10:00:00", description = "The calculated date for the next automated transaction.")
        LocalDateTime nextExecutionDate,

        @Schema(example = "ACTIVE", implementation = PlanStatus.class, description = "Current status of the plan (e.g., ACTIVE, PAUSED, FINISHED).")
        PlanStatus status,

        @Schema(example = "2027-01-01T10:00:00", description = "Optional end date. If null, the plan is indefinite.")
        LocalDateTime endDate,

        @Schema(description = "The full category object associated with this plan.")
        CategoryResponseDTO category,

        @Schema(example = "1", description = "ID of the wallet where this plan operates.")
        Long walletId,

        @Schema(example = "2026-01-01T08:00:00", description = "Timestamp when the record was created.")
        LocalDateTime createdAt,

        @Schema(example = "2026-04-15T12:00:00", description = "Timestamp of the last modification.")
        LocalDateTime updatedAt
) {
}
