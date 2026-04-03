package com.returdev.nexflow.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;


class MapperTest {

    private CategoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CategoryMapper();
    }

    @Test
    void normalizeDateToUTC_WithOtherTimeZone_ReturnsLocalDateTimeInUTC() {

        LocalDateTime targetDate = LocalDateTime.of(2026, 2, 1, 12, 20);
        OffsetDateTime dateToNormalize = OffsetDateTime.of(
                targetDate.plusHours(5),
                ZoneOffset.of("+05:00")
        );

        LocalDateTime normalizedDate = mapper.normalizeDateToUTC(dateToNormalize);

        assertThat(normalizedDate)
                .isEqualTo(targetDate);

    }

    @Test
    void normalizeDateToUTC_WithUTCTimeZone_ReturnsSameLocalDateTime() {

        LocalDateTime targetDate = LocalDateTime.of(2026, 2, 1, 12, 20);
        OffsetDateTime dateToNormalize = OffsetDateTime.of(
                targetDate,
                ZoneOffset.UTC
        );

        LocalDateTime normalizedDate = mapper.normalizeDateToUTC(dateToNormalize);

        assertThat(normalizedDate)
                .isEqualTo(targetDate);

    }


}