package com.tools.cookieTycoon.listeners;

import com.tools.cookieTycoon.game.AchievementManager;
import com.tools.cookieTycoon.game.RebirthManager;
import com.tools.cookieTycoon.game.UpgradesManager;
import com.tools.cookieTycoon.model.UpgradeType;
import com.tools.cookieTycoon.ui.Menu;
import com.tools.cookieTycoon.ui.Menus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;


public final class MenuListener implements Listener {

    private static final int REBIRTH_SLOT = 4;

    private final Menus menus;
    private final UpgradesManager upgrades;
    private final AchievementManager achievements;
    private final RebirthManager rebirth;

    public MenuListener(Menus menus, UpgradesManager upgrades, AchievementManager achievements,
                        RebirthManager rebirth) {
        this.menus = menus;
        this.upgrades = upgrades;
        this.achievements = achievements;
        this.rebirth = rebirth;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Menu menu = menus.menuOf(player, event.getView().getTopInventory());
        if (menu == null) {
            return;
        }


        event.setCancelled(true);
        if (event.getClickedInventory() != menu.inventory()) {
            return;
        }

        int slot = event.getRawSlot();
        switch (menu.type()) {
            case UPGRADES:
                onUpgradeClick(player, menu, slot, event.getClick());
                break;
            case ACHIEVEMENTS:
                int id = menu.achievementAt(slot);
                if (id >= 0) {
                    achievements.buy(player, id);
                    menus.refresh(player);
                }
                break;
            case REBIRTH:
                if (slot == REBIRTH_SLOT) {
                    rebirth.buy(player);
                    player.closeInventory();
                }
                break;
            default:
                break;
        }
    }

    private void onUpgradeClick(Player player, Menu menu, int slot, ClickType click) {
        UpgradeType type = menu.upgradeAt(slot);
        if (type == null) {
            return;
        }
        switch (click) {
            case LEFT:
                upgrades.buy(player, type, 1);
                break;
            case RIGHT:
                upgrades.buy(player, type, 10);
                break;
            case MIDDLE:
                upgrades.buy(player, type, 100);
                break;
            case SHIFT_LEFT:
                upgrades.sell(player, type, 1);
                break;
            case SHIFT_RIGHT:
                upgrades.sell(player, type, 10);
                break;
            default:
                return;
        }
        menus.refresh(player);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player
                && menus.menuOf((Player) event.getWhoClicked(), event.getView().getTopInventory()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            menus.onClosed((Player) event.getPlayer(), event.getInventory());
        }
    }
}
