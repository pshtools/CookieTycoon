package com.tools.cookieTycoon.utils;


public final class TimeFormatter {

    private static final long MINUTE_MS = 60_000L;

    private TimeFormatter() {
    }

    public static String format(long millis) {
        long totalMinutes = Math.max(0, millis) / MINUTE_MS;

        long years = totalMinutes / (60 * 24 * 365);
        long remaining = totalMinutes % (60 * 24 * 365);

        long months = remaining / (60 * 24 * 30);
        remaining %= 60 * 24 * 30;

        long weeks = remaining / (60 * 24 * 7);
        remaining %= 60 * 24 * 7;

        long days = remaining / (60 * 24);
        remaining %= 60 * 24;

        long hours = remaining / 60;
        long minutes = remaining % 60;

        StringBuilder sb = new StringBuilder();
        if (years > 0) sb.append(years).append("г ");
        if (months > 0) sb.append(months).append("мес ");
        if (weeks > 0) sb.append(weeks).append("нед ");
        if (days > 0) sb.append(days).append("д ");
        if (hours > 0) sb.append(hours).append("ч ");
        if (minutes > 0 || sb.length() == 0) sb.append(minutes).append("мин");
        return sb.toString().trim();
    }
}
