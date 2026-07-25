package com.arsalan.tenanttable.common.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public final class DateRangeUtil {

    private static final ZoneId ZONE = ZoneId.systemDefault();

    private DateRangeUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static LocalDate today() {
        return LocalDate.now(ZONE);
    }

    public static Instant startOfToday() {
        return today().atStartOfDay(ZONE).toInstant();
    }

    public static Instant startOfTomorrow() {
        return today().plusDays(1).atStartOfDay(ZONE).toInstant();
    }

    public static LocalDate firstDayOfCurrentMonth() {
        return today().withDayOfMonth(1);
    }

    public static LocalDate firstDayOfNextMonth() {
        return firstDayOfCurrentMonth().plusMonths(1);
    }

    public static Instant startOfCurrentMonth() {
        return firstDayOfCurrentMonth().atStartOfDay(ZONE).toInstant();
    }

    public static Instant startOfNextMonth() {
        return firstDayOfNextMonth().atStartOfDay(ZONE).toInstant();
    }

    public static Instant startOfDay(LocalDate date) {
        return date.atStartOfDay(ZONE).toInstant();
    }

    public static Instant startOfNextDay(LocalDate date) {
        return date.plusDays(1).atStartOfDay(ZONE).toInstant();
    }
}