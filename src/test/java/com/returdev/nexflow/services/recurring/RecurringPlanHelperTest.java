package com.returdev.nexflow.services.recurring;

import com.returdev.nexflow.model.entities.RecurringPlanEntity;
import com.returdev.nexflow.model.enums.Frequency;
import com.returdev.nexflow.model.exceptions.DateConflictException;
import com.returdev.nexflow.utils.TestEntityFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RecurringPlanHelperTest {

    private final RecurringPlanHelper helper = new RecurringPlanHelper();

    @Test
    void calculateNextExecutionDate_WhenIsFirstExecution_ReturnsTheStartDate() {

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startDate = today.plusMonths(2);

        RecurringPlanEntity entity = RecurringPlanEntity.builder()
                .startDate(startDate)
                .interval(1)
                .frequency(Frequency.MONTHLY)
                .build();


        LocalDateTime nextExecutionDate = helper.calculateNextExecutionDate(entity);


        assertThat(nextExecutionDate)
                .isEqualTo(startDate);

    }

    @Test
    void calculateNextExecutionDate_WhenIsFirstExecutionAndStartDateIsPast_ReturnsCorrectNextExecutionDate() {

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startDate = today.minusMonths(5);

        RecurringPlanEntity entity = RecurringPlanEntity.builder()
                .startDate(startDate)
                .interval(1)
                .frequency(Frequency.MONTHLY)
                .build();


        LocalDateTime nextExecutionDate = helper.calculateNextExecutionDate(entity);

        assertThat(nextExecutionDate)
                .isEqualTo(today);

    }

    @Test
    void calculateNextExecutionDate_WhenIntervalGreaterOfOne_ReturnsCorrectNextExecutionDate() {

        LocalDateTime today = LocalDateTime.now();
        int interval = 5;
        LocalDateTime nextDateExpected = today.plusMonths(interval);

        RecurringPlanEntity entity = RecurringPlanEntity.builder()
                .startDate(today)
                .lastExecutionDate(today)
                .interval(interval)
                .frequency(Frequency.MONTHLY)
                .build();


        LocalDateTime nextExecutionDate = helper.calculateNextExecutionDate(entity);


        assertThat(nextExecutionDate)
                .isAfter(today)
                .isEqualTo(nextDateExpected);

    }

    @Test
    void calculateNextExecutionDate_WhenIsEndOfMonth_ReturnsCorrectNextExecutionDate() {

        LocalDateTime startDate = LocalDateTime.of(2020, 3, 31, 0, 0);
        LocalDate fakeToday = LocalDate.of(2020, 3, 31);
        int interval = 1;
        LocalDateTime nextDateExpected = LocalDateTime.of(2020, 4, 30, 0, 0);

        RecurringPlanEntity entity = RecurringPlanEntity.builder()
                .startDate(startDate)
                .lastExecutionDate(startDate)
                .interval(interval)
                .frequency(Frequency.MONTHLY)
                .build();

        try(MockedStatic<LocalDate> localDate = Mockito.mockStatic(LocalDate.class,Mockito.CALLS_REAL_METHODS)) {
            localDate.when(LocalDate::now).thenReturn(fakeToday);
            LocalDateTime nextExecutionDate = helper.calculateNextExecutionDate(entity);

            assertThat(nextExecutionDate)
                    .isEqualTo(nextDateExpected);

        }

    }

    @Test
    void calculateNextExecutionDate_WhenLeapYear_ReturnsCorrectNextExecutionDate() {

        LocalDateTime startDate = LocalDateTime.of(2020, 2, 29, 0, 0);
        LocalDate fakeToday = LocalDate.of(2020, 2, 29);
        int interval = 1;
        LocalDateTime nextDateExpected = LocalDateTime.of(2021, 2, 28, 0, 0);

        RecurringPlanEntity entity = RecurringPlanEntity.builder()
                .startDate(startDate)
                .lastExecutionDate(startDate)
                .interval(interval)
                .frequency(Frequency.ANNUALLY)
                .build();

        try(MockedStatic<LocalDate> localDate = Mockito.mockStatic(LocalDate.class,Mockito.CALLS_REAL_METHODS)) {
            localDate.when(LocalDate::now).thenReturn(fakeToday);
            LocalDateTime nextExecutionDate = helper.calculateNextExecutionDate(entity);

            assertThat(nextExecutionDate)
                    .isEqualTo(nextDateExpected);

        }

    }

    @Test
    void verifyNextExecutionDateOnChanges_WhenNextExecutionIsAfterEndDate_ShouldThrowException() {
        LocalDateTime nextExecutionDate = LocalDateTime.now();
        LocalDateTime endDate = nextExecutionDate.minusMonths(1);
        RecurringPlanEntity entity = RecurringPlanEntity.builder()
                        .nextExecutionDate(nextExecutionDate)
                                .endDate(endDate)
                .build();

        assertThrows(DateConflictException.class, () -> helper.verifyNextExecutionDateOnChanges(entity));

    }

    @Test
    void verifyDates_WhenStartDateIsBeforeToday_ShouldThrowException() {

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startDate = today.minusMonths(1);

        assertThrows(DateConflictException.class, () -> helper.verifyDates(startDate, null));

    }

    @Test
    void verifyDates_WhenStartDateIsAfterEndDate_ShouldThrowException() {

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startDate = today.plusMonths(1);
        LocalDateTime endDate = today.minusMonths(1);

        assertThrows(DateConflictException.class, () -> helper.verifyDates(startDate, endDate));

    }
}