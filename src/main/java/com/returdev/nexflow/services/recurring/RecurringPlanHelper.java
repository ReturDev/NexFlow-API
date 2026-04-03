package com.returdev.nexflow.services.recurring;

import com.returdev.nexflow.model.entities.RecurringPlanEntity;
import com.returdev.nexflow.model.enums.Frequency;
import com.returdev.nexflow.model.exceptions.BusinessException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Component
public class RecurringPlanHelper {

    /**
     * Calculates the chronologically correct next execution date for a plan.
     * <p>
     * <b>Scheduling Logic:</b>
     * 1. If the plan has never been executed and the start date is in the future, it returns the start date.
     * 2. Otherwise, it calculates a {@link Period} based on frequency and interval.
     * 3. It then "jumps forward" from the last execution (or start date) by adding the period
     * repeatedly until it finds a date that is not in the past relative to {@code LocalDateTime.now()}.
     * </p>
     *
     * @param entity the recurring plan entity containing scheduling parameters.
     * @return the calculated {@link LocalDateTime} for the next execution.
     */
    public LocalDateTime calculateNextExecutionDate(RecurringPlanEntity entity) {


        verifyDates(entity.getStartDate(), entity.getEndDate());

        LocalDate today = LocalDate.now();

        if (entity.getLastExecutionDate() == null && !entity.getStartDate().toLocalDate().isBefore(today)) {
            return entity.getStartDate();
        }

        Period period = getPeriod(entity.getFrequency(), entity.getInterval());

        LocalDateTime next = (entity.getLastExecutionDate() != null)
                ? entity.getLastExecutionDate()
                : entity.getStartDate();

        while (next.toLocalDate().isBefore(today)) {
            next = next.plus(period);
        }

        return next;

    }

    /**
     * Validates that an update to the plan doesn't push the next execution
     * past the plan's definitive end date.
     *
     * @param recurringPlan the plan entity after changes have been applied.
     * @throws BusinessException if the next execution date exceeds the end date.
     */
    public void verifyNextExecutionDateOnChanges(RecurringPlanEntity recurringPlan) {

        if (recurringPlan.getEndDate() != null && recurringPlan.getNextExecutionDate() != null) {
            if (recurringPlan.getEndDate().toLocalDate().isBefore(recurringPlan.getNextExecutionDate().toLocalDate())) {
                throw new BusinessException("exception.recurring_plan.updating_next_execution_error");
            }
        }

    }

    /**
     * Enforces fundamental date constraints for recurring plans.
     *
     * @param startDateTime the requested start of the plan.
     * @param endDateTime   the requested end of the plan (nullable).
     * @throws BusinessException if dates are chronologically invalid.
     */
    public void verifyDates(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = startDateTime.toLocalDate();

        if (startDate.isBefore(now)) {
            throw new BusinessException("exception.recurring.start_date_in_past");
        }

        if (endDateTime != null) {
            LocalDate endDate = endDateTime.toLocalDate();
            if (startDate.isAfter(endDate)) {
                throw new BusinessException("exception.recurring.start_date_after_end_date");
            }
        }
    }

    /**
     * Maps the business {@link Frequency} and interval to a Java {@link Period}.
     *
     * @param frequency the repeat unit (Daily, Weekly, etc.).
     * @param interval  the multiplier for the frequency.
     * @return a {@link Period} representing the time jump between executions.
     */
    private Period getPeriod(Frequency frequency, Integer interval) {
        return switch (frequency) {
            case DAILY -> Period.ofDays(interval);
            case WEEKLY -> Period.ofWeeks(interval);
            case MONTHLY -> Period.ofMonths(interval);
            case ANNUALLY -> Period.ofYears(interval);
        };
    }


}
