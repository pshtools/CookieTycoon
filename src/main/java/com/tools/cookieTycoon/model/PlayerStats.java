package com.tools.cookieTycoon.model;

import java.util.BitSet;


public final class PlayerStats {

    private final double[] perUpgrade;
    private final double cps;
    private final double clickValue;

    private PlayerStats(double[] perUpgrade, double cps, double clickValue) {
        this.perUpgrade = perUpgrade;
        this.cps = cps;
        this.clickValue = clickValue;
    }

    
    public static PlayerStats compute(int[] counts, BitSet owned) {
        final int n = UpgradeType.COUNT;

        double[] multiplier = new double[n];
        double[] addend = new double[n];
        for (int i = 0; i < n; i++) {
            multiplier[i] = 1.0;
        }

        for (int id = owned.nextSetBit(0); id >= 0; id = owned.nextSetBit(id + 1)) {
            Achievement a = Achievements.get(id);
            if (a == null) {
                continue;
            }
            int t = a.type().ordinal();
            if (a.isMultiplicative()) {
                multiplier[t] *= a.bonus();
            } else {
                addend[t] += a.bonus();
            }
        }

        double totalCount = 0;
        for (int c : counts) {
            totalCount += c;
        }

        double[] perUpgrade = new double[n];
        double cps = 0;
        for (int i = 0; i < n; i++) {
            UpgradeType type = UpgradeType.byOrdinal(i);
            double sumExcept = totalCount - counts[i];
            perUpgrade[i] = (type.baseCps() * multiplier[i] + addend[i] * sumExcept) * counts[i];
            cps += perUpgrade[i];
        }

        int cursor = UpgradeType.CURSOR.ordinal();
        double click = multiplier[cursor] + addend[cursor] * (totalCount - counts[cursor]);

        return new PlayerStats(perUpgrade, cps, click);
    }

    
    public double cps() {
        return cps;
    }

    
    public double cpsOf(UpgradeType type) {
        return perUpgrade[type.ordinal()];
    }

    
    public double clickValue() {
        return clickValue;
    }
}
