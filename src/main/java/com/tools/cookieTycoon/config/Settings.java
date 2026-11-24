package com.tools.cookieTycoon.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Settings {

    private final String connectionString;
    private final String databaseName;
    private final int autosaveSeconds;
    private final boolean protectInventory;

    private Settings(String connectionString, String databaseName, int autosaveSeconds, boolean protectInventory) {
        this.connectionString = connectionString;
        this.databaseName = databaseName;
        this.autosaveSeconds = autosaveSeconds;
        this.protectInventory = protectInventory;
    }

    public static Settings load(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        return new Settings(
                config.getString("connection-string", "mongodb://localhost:27017"),
                config.getString("db-name", "cookieTycoon"),
                Math.max(10, config.getInt("autosave-seconds", 60)),
                config.getBoolean("protect-inventory", true));
    }

    public String connectionString() {
        return connectionString;
    }

    public String databaseName() {
        return databaseName;
    }

    public int autosaveSeconds() {
        return autosaveSeconds;
    }

    public boolean protectInventory() {
        return protectInventory;
    }
}
