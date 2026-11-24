package com.tools.cookieTycoon.model;


public final class Achievement {

    private final int id;
    private final UpgradeType type;
    private final String name;
    private final double price;
    private final double bonus;
    private final boolean multiplicative;
    private final int requiredCount;

    public Achievement(int id, UpgradeType type, String name, double price, double bonus,
                       boolean multiplicative, int requiredCount) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.price = price;
        this.bonus = bonus;
        this.multiplicative = multiplicative;
        this.requiredCount = requiredCount;
    }

    public int id() {
        return id;
    }

    
    public UpgradeType type() {
        return type;
    }

    public String name() {
        return name;
    }

    public double price() {
        return price;
    }

    
    public double bonus() {
        return bonus;
    }

    public boolean isMultiplicative() {
        return multiplicative;
    }

    
    public int requiredCount() {
        return requiredCount;
    }
}
