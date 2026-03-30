package com.returdev.nexflow.model.enums;

/**
 * Defines the unit of time used to calculate the recurrence of a {@code RecurringPlanEntity}.
 * <p>
 * This enum is used in conjunction with an interval to determine the next execution
 * date of a scheduled transaction. For example, a frequency of {@code WEEKLY} with
 * an interval of {@code 2} results in a bi-weekly schedule.
 */
public enum Frequency {

    DAILY,
    WEEKLY,
    MONTHLY,
    ANNUALLY

}
