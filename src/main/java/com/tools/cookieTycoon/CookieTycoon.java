package com.tools.cookieTycoon;

import com.tools.cookieTycoon.commands.AdminCommands;
import com.tools.cookieTycoon.commands.PlayerCommands;
import com.tools.cookieTycoon.config.Settings;
import com.tools.cookieTycoon.data.LegacyData;
import com.tools.cookieTycoon.data.PlayerDataManager;
import com.tools.cookieTycoon.game.AchievementManager;
import com.tools.cookieTycoon.game.CookieManager;
import com.tools.cookieTycoon.game.GameLoop;
import com.tools.cookieTycoon.game.RebirthManager;
import com.tools.cookieTycoon.game.UpgradesManager;
import com.tools.cookieTycoon.listeners.ClickListener;
import com.tools.cookieTycoon.listeners.MenuListener;
import com.tools.cookieTycoon.listeners.ProtectionListener;
import com.tools.cookieTycoon.listeners.SessionListener;
import com.tools.cookieTycoon.storage.MongoStorage;
import com.tools.cookieTycoon.ui.ItemFactory;
import com.tools.cookieTycoon.ui.Menus;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;


public final class CookieTycoon extends JavaPlugin {

    private MongoStorage storage;
    private PlayerDataManager players;
    private GameLoop loop;
    private Menus menus;

    @Override
    public void onEnable() {
        Settings settings = Settings.load(this);

        try {
            storage = new MongoStorage(settings.connectionString(), settings.databaseName());
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Не удалось подключиться к MongoDB — плагин отключается", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Успешное подключение к MongoDB!");

        ItemFactory items;
        try {
            items = new ItemFactory();
        } catch (IllegalArgumentException e) {
            getLogger().log(Level.SEVERE, "В UpgradeType указан неизвестный материал", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        players = new PlayerDataManager(getLogger(), storage, LegacyData.load(getDataFolder(), getLogger()));

        CookieManager cookies = new CookieManager(players);
        UpgradesManager upgrades = new UpgradesManager(players);
        AchievementManager achievements = new AchievementManager(players);
        RebirthManager rebirth = new RebirthManager(players);

        menus = new Menus(players, achievements, items);
        loop = new GameLoop(this, cookies, items);

        SessionListener session = new SessionListener(getLogger(), players, loop, menus);
        getServer().getPluginManager().registerEvents(session, this);
        getServer().getPluginManager().registerEvents(new ClickListener(players, cookies), this);
        getServer().getPluginManager().registerEvents(new MenuListener(menus, upgrades, achievements, rebirth), this);
        if (settings.protectInventory()) {
            getServer().getPluginManager().registerEvents(new ProtectionListener(), this);
        }

        AdminCommands admin = new AdminCommands(players, cookies, upgrades);
        for (String name : new String[]{"ck", "upgrade"}) {
            PluginCommand command = getCommand(name);
            command.setExecutor(admin);
            command.setTabCompleter(admin);
        }
        PlayerCommands playerCommands = new PlayerCommands(menus);
        for (String name : new String[]{"menu", "rebirth", "ach", "info"}) {
            getCommand(name).setExecutor(playerCommands);
        }

        long autosaveTicks = settings.autosaveSeconds() * 20L;
        getServer().getScheduler().runTaskTimer(this, players::autosave, autosaveTicks, autosaveTicks);
        loop.start();


        for (Player online : Bukkit.getOnlinePlayers()) {
            try {
                players.preload(online.getUniqueId());
                session.setUp(online);
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Не удалось загрузить данные " + online.getName(), e);
                online.kickPlayer("§cНе удалось загрузить ваши данные. Попробуйте зайти позже.");
            }
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        if (loop != null) {
            loop.shutdown();
        }
        if (players != null) {
            players.shutdown();
        }
        if (storage != null) {
            storage.close();
        }
    }
}
