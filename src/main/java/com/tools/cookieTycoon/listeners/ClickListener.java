package com.tools.cookieTycoon.listeners;

import com.tools.cookieTycoon.data.PlayerDataManager;
import com.tools.cookieTycoon.game.CookieManager;
import com.tools.cookieTycoon.model.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


public final class ClickListener implements Listener {

    private final PlayerDataManager players;
    private final CookieManager cookies;

    public ClickListener(PlayerDataManager players, CookieManager cookies) {
        this.players = players;
        this.cookies = cookies;
    }


    @EventHandler(ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR) {
            return;
        }
        PlayerData data = players.get(event.getPlayer().getUniqueId());
        if (data != null) {
            cookies.click(data);
        }
    }
}
