package com.campuscomplaint.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    public static String formatDate(LocalDateTime date) {
        if (date == null) return "N/A";
        return date.format(DATE_FORMATTER);
    }

    public static String formatTime(LocalDateTime time) {
        if (time == null) return "N/A";
        return time.format(TIME_FORMATTER);
    }
}