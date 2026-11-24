package com.tools.cookieTycoon.ui;

import com.tools.cookieTycoon.model.UpgradeType;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;


public final class Menu {

    public enum Type { UPGRADES, ACHIEVEMENTS, REBIRTH, INFO }

    private final Type type;
    private final Inventory inventory;
    private final UpgradeType[] upgradeSlots;
    private final int[] achievementSlots;

    Menu(Type type, Inventory inventory) {
        this.type = type;
        this.inventory = inventory;
        this.upgradeSlots = type == Type.UPGRADES ? new UpgradeType[inventory.getSize()] : null;
        this.achievementSlots = type == Type.ACHIEVEMENTS ? new int[inventory.getSize()] : null;
    }

    public Type type() {
        return type;
    }

    public Inventory inventory() {
        return inventory;
    }

    
    public UpgradeType upgradeAt(int slot) {
        return upgradeSlots != null && slot >= 0 && slot < upgradeSlots.length ? upgradeSlots[slot] : null;
    }

    
    public int achievementAt(int slot) {
        return achievementSlots != null && slot >= 0 && slot < achievementSlots.length ? achievementSlots[slot] : -1;
    }

    void clearSlots() {
        if (upgradeSlots != null) {
            Arrays.fill(upgradeSlots, null);
        }
        if (achievementSlots != null) {
            Arrays.fill(achievementSlots, -1);
        }
    }

    void setUpgrade(int slot, UpgradeType value) {
        upgradeSlots[slot] = value;
    }

    void setAchievement(int slot, int id) {
        achievementSlots[slot] = id;
    }
}
