package com.tools.cookieTycoon.model;

import java.util.BitSet;
import java.util.UUID;


public final class PlayerData {

    private final UUID uuid;

    private double cookies;
    private double allCookies;
    private int rebirth;

    private final int[] upgrades = new int[UpgradeType.COUNT];
    private final BitSet achievements = new BitSet();


    private long playTimeMs;
    private long rebirthPlayTimeMs;
    private long sessionStartMs;
    private long rebirthMarkMs;

    private PlayerStats stats;
    private int revision;
    private boolean dirty;
    private long lastAccrualNanos;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID uuid() {
        return uuid;
    }



    public double getCookies() {
        return cookies;
    }

    public void setCookies(double value) {
        this.cookies = sanitize(value);
        dirty = true;
    }

    
    public double getAllCookies() {
        return allCookies;
    }

    public void setAllCookies(double value) {
        this.allCookies = sanitize(value);
        dirty = true;
    }

    
    public void earn(double amount) {
        if (!(amount > 0)) {
            return;
        }
        cookies += amount;
        allCookies += amount;
        dirty = true;
    }

    
    public void spend(double amount) {
        setCookies(Math.max(0, cookies - amount));
    }

    
    public void refund(double amount) {
        if (amount > 0) {
            setCookies(cookies + amount);
        }
    }

    private static double sanitize(double value) {
        return Double.isFinite(value) ? Math.max(0, value) : 0;
    }



    public int getRebirth() {
        return rebirth;
    }

    public void setRebirth(int value) {
        this.rebirth = Math.max(0, value);
        dirty = true;
    }



    public int getUpgrade(UpgradeType type) {
        return upgrades[type.ordinal()];
    }

    public void setUpgrade(UpgradeType type, int amount) {
        upgrades[type.ordinal()] = Math.max(0, amount);
        invalidate();
    }

    public void addUpgrade(UpgradeType type, int delta) {
        long result = (long) upgrades[type.ordinal()] + delta;
        setUpgrade(type, (int) Math.max(0, Math.min(Integer.MAX_VALUE, result)));
    }

    public void clearUpgrades() {
        java.util.Arrays.fill(upgrades, 0);
        invalidate();
    }

    
    public int[] upgradeCounts() {
        return upgrades.clone();
    }



    public boolean hasAchievement(int id) {
        return achievements.get(id);
    }

    public void addAchievement(int id) {
        if (id >= 0 && !achievements.get(id)) {
            achievements.set(id);
            invalidate();
        }
    }

    public int[] achievementIds() {
        return achievements.stream().toArray();
    }



    
    public PlayerStats stats() {
        PlayerStats s = stats;
        if (s == null) {
            s = PlayerStats.compute(upgrades, achievements);
            stats = s;
        }
        return s;
    }

    
    public int revision() {
        return revision;
    }

    private void invalidate() {
        stats = null;
        revision++;
        dirty = true;
    }



    public void startSession(long nowMs) {
        sessionStartMs = nowMs;
        rebirthMarkMs = nowMs;
    }

    public long totalPlayTimeMs(long nowMs) {
        return playTimeMs + Math.max(0, nowMs - sessionStartMs);
    }

    public long rebirthPlayTimeMs(long nowMs) {
        return rebirthPlayTimeMs + Math.max(0, nowMs - Math.max(sessionStartMs, rebirthMarkMs));
    }

    
    public void resetRebirthTimer(long nowMs) {
        rebirthPlayTimeMs = 0;
        rebirthMarkMs = nowMs;
        dirty = true;
    }

    
    public void loadPlayTime(long totalMs, long rebirthMs) {
        this.playTimeMs = Math.max(0, totalMs);
        this.rebirthPlayTimeMs = Math.max(0, rebirthMs);
    }



    public boolean isDirty() {
        return dirty;
    }

    public void markDirty() {
        dirty = true;
    }

    public void clearDirty() {
        dirty = false;
    }



    public long lastAccrualNanos() {
        return lastAccrualNanos;
    }

    public void lastAccrualNanos(long nanos) {
        this.lastAccrualNanos = nanos;
    }
}
