package com.returdev.nexflow.dto.response;

import com.returdev.nexflow.model.enums.Frequency;
import com.returdev.nexflow.model.enums.PlanStatus;
import com.returdev.nexflow.model.enums.TransactionType;

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
public record RecurringPlanResponseDTO(
        Long id,
        String title,
        String description,
        Long balanceInCents,
        TransactionType type,
        LocalDateTime startDate,
        Frequency frequency,
        Integer interval,
        LocalDateTime nextExecutionDate,
        PlanStatus status,
        LocalDateTime endDate,
        CategoryResponseDTO category,
        Long walletId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
