package com.tools.cookieTycoon.utils;

import java.text.DecimalFormat;


public final class NumberFormatter {

    private static final String[] SUFFIXES = {
            "", "K", "M", "B", "T", "Q", "Qt", "Sx", "Sp", "Oc", "Non", "Dec", "Und", "Dod", "Trd", "Qtd"
    };

    
    private static final double[] POWERS = new double[SUFFIXES.length];

    static {
        for (int i = 0; i < POWERS.length; i++) {
            POWERS[i] = Double.parseDouble("1e" + (3 * i));
        }
    }

    private static final ThreadLocal<DecimalFormat> NUMBER =
            ThreadLocal.withInitial(() -> new DecimalFormat("#.##"));
    private static final ThreadLocal<DecimalFormat> PERCENT =
            ThreadLocal.withInitial(() -> new DecimalFormat("#.##%"));

    private NumberFormatter() {
    }

    public static String format(double number) {
        if (number < 1_000_000) {
            return NUMBER.get().format(number);
        }



        int exp = SUFFIXES.length - 1;
        while (exp > 2 && number < POWERS[exp]) {
            exp--;
        }
        double scaled = number / POWERS[exp];
        return NUMBER.get().format(scaled) + SUFFIXES[exp];
    }

    public static String formatPercent(double fraction) {
        return PERCENT.get().format(fraction);
    }
}
