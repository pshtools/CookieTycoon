package com.tools.cookieTycoon.game;

import com.tools.cookieTycoon.data.PlayerDataManager;
import com.tools.cookieTycoon.model.PlayerData;
import com.tools.cookieTycoon.model.UpgradeType;
import com.tools.cookieTycoon.utils.NumberFormatter;
import org.bukkit.entity.Player;


public final class UpgradesManager {


    private static final double SELL_REFUND_1 = 0.25;
    private static final double SELL_REFUND_10 = 1.254;
    private static final double SELL_REFUND_100 = 1.198;

    private final PlayerDataManager players;

    public UpgradesManager(PlayerDataManager players) {
        this.players = players;
    }

    
    public static double bulkPrice(PlayerData data, UpgradeType type, int amount) {
        return type.price(data.getUpgrade(type)) * UpgradeType.bulkFactor(amount);
    }

    
    public boolean buy(Player player, UpgradeType type, int amount) {
        PlayerData data = players.require(player.getUniqueId());
        double cost = bulkPrice(data, type, amount);
        if (data.getCookies() < cost) {
            player.sendMessage("У вас недостаточно средств");
            return false;
        }
        data.spend(cost);
        data.addUpgrade(type, amount);
        return true;
    }

    
    public boolean sell(Player player, UpgradeType type, int amount) {
        PlayerData data = players.require(player.getUniqueId());
        int owned = data.getUpgrade(type);
        if (owned < amount) {
            player.sendMessage(amount == 1 ? "У вас нет улучшения" : "У вас нет столько улучшений");
            return false;
        }

        double price = type.price(owned);
        double refund;
        switch (amount) {
            case 1:
                refund = price * SELL_REFUND_1;
                break;
            case 10:
                refund = price * SELL_REFUND_10;
                break;
            case 100:
                refund = price * SELL_REFUND_100;
                break;
            default:
                return false;
        }

        data.refund(refund);
        data.addUpgrade(type, -amount);
        if (amount > 1) {
            player.sendMessage("+" + NumberFormatter.format(refund));
        }
        return true;
    }

    public int get(Player player, UpgradeType type) {
        return players.require(player.getUniqueId()).getUpgrade(type);
    }

    public void set(Player player, UpgradeType type, int amount) {
        players.require(player.getUniqueId()).setUpgrade(type, amount);
    }

    public void add(Player player, UpgradeType type, int delta) {
        players.require(player.getUniqueId()).addUpgrade(type, delta);
    }

    public void wipe(Player player) {
        players.require(player.getUniqueId()).clearUpgrades();
    }
}
