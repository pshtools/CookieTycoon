package com.tools.cookieTycoon.game;

import com.tools.cookieTycoon.data.PlayerDataManager;
import com.tools.cookieTycoon.model.PlayerData;
import org.bukkit.entity.Player;


public final class CookieManager {

    
    private static final double MAX_CATCHUP_SECONDS = 10;

    private final PlayerDataManager players;

    public CookieManager(PlayerDataManager players) {
        this.players = players;
    }

    public double get(Player player) {
        return players.require(player.getUniqueId()).getCookies();
    }

    public double getAll(Player player) {
        return players.require(player.getUniqueId()).getAllCookies();
    }

    public double getCps(Player player) {
        return players.require(player.getUniqueId()).stats().cps();
    }

    public void set(Player player, double cookies) {
        players.require(player.getUniqueId()).setCookies(cookies);
    }

    
    public void add(Player player, double amount) {
        players.require(player.getUniqueId()).earn(amount);
    }

    public void remove(Player player, double amount) {
        players.require(player.getUniqueId()).spend(amount);
    }

    public void wipe(Player player) {
        set(player, 0);
    }

    
    public void click(PlayerData data) {
        data.earn(data.stats().clickValue());
    }

    
    public void accrue(PlayerData data, long nowNanos) {
        long last = data.lastAccrualNanos();
        data.lastAccrualNanos(nowNanos);

        double seconds = Math.min((nowNanos - last) / 1e9, MAX_CATCHUP_SECONDS);
        if (seconds > 0) {
            data.earn(data.stats().cps() * seconds);
        }
    }
}
