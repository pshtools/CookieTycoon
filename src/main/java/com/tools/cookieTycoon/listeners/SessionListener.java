package com.tools.cookieTycoon.listeners;

import com.tools.cookieTycoon.data.PlayerDataManager;
import com.tools.cookieTycoon.game.GameLoop;
import com.tools.cookieTycoon.model.PlayerData;
import com.tools.cookieTycoon.ui.Menus;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Level;
import java.util.logging.Logger;


public final class SessionListener implements Listener {

    private static final String LOAD_FAILED = "§cНе удалось загрузить ваши данные. Попробуйте зайти позже.";

    private final Logger log;
    private final PlayerDataManager players;
    private final GameLoop loop;
    private final Menus menus;

    public SessionListener(Logger log, PlayerDataManager players, GameLoop loop, Menus menus) {
        this.log = log;
        this.players = players;
        this.loop = loop;
        this.menus = menus;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }
        try {
            players.preload(event.getUniqueId());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Не удалось загрузить данные " + event.getName(), e);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, LOAD_FAILED);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        setUp(event.getPlayer());
    }

    
    public void setUp(Player player) {
        PlayerData data = players.activate(player.getUniqueId(), player.getName());
        if (data == null) {
            player.kickPlayer(LOAD_FAILED);
            return;
        }
        player.setGameMode(GameMode.ADVENTURE);
        loop.attach(player, data);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        loop.detach(player);
        menus.forget(player);
        players.unload(player.getUniqueId());
    }
}
