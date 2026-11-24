package com.tools.cookieTycoon.game;

import com.tools.cookieTycoon.data.PlayerDataManager;
import com.tools.cookieTycoon.model.PlayerData;
import com.tools.cookieTycoon.utils.NumberFormatter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;


public final class RebirthManager {

    private final PlayerDataManager players;

    public RebirthManager(PlayerDataManager players) {
        this.players = players;
    }

    
    public static double price(int currentRebirth) {
        return Math.pow(10, 12) * Math.pow(1 + currentRebirth, 3);
    }

    public static boolean isAvailable(PlayerData data) {
        return data.getAllCookies() >= price(data.getRebirth());
    }

    public void buy(Player player) {
        PlayerData data = players.require(player.getUniqueId());

        if (!isAvailable(data)) {
            player.sendMessage("§cВам не хватает "
                    + NumberFormatter.format(price(data.getRebirth()) - data.getAllCookies())
                    + " до следующего уровня.");
            return;
        }

        data.setCookies(0);
        data.clearUpgrades();
        data.resetRebirthTimer(System.currentTimeMillis());
        data.setRebirth(data.getRebirth() + 1);

        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        player.sendTitle("§6Престиж повышен!", "§eВаш уровень §f- §a" + data.getRebirth());
    }
}
