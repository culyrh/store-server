package com.bookstore.api.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * LocalDateTime을 문자열로 변환 (기본 포맷)
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }

    /**
     * LocalDateTime을 문자열로 변환 (날짜만)
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_FORMATTER);
    }

    /**
     * 문자열을 LocalDateTime으로 변환
     */
    public static LocalDateTime parse(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER);
    }

    /**
     * 현재 시각을 문자열로 반환
     */
    public static String now() {
        return format(LocalDateTime.now());
    }
}