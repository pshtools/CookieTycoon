package com.tools.cookieTycoon.ui;

import com.tools.cookieTycoon.data.PlayerDataManager;
import com.tools.cookieTycoon.game.AchievementManager;
import com.tools.cookieTycoon.model.Achievement;
import com.tools.cookieTycoon.model.PlayerData;
import com.tools.cookieTycoon.model.UpgradeType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public final class Menus {

    private static final int ACHIEVEMENT_SLOTS = 27;

    private final PlayerDataManager players;
    private final AchievementManager achievements;
    private final ItemFactory items;
    private final Map<UUID, Menu> open = new HashMap<>();

    public Menus(PlayerDataManager players, AchievementManager achievements, ItemFactory items) {
        this.players = players;
        this.achievements = achievements;
        this.items = items;
    }

    

    public void openUpgrades(Player player) {
        PlayerData data = players.get(player.getUniqueId());
        if (data == null) {
            return;
        }
        Menu menu = new Menu(Menu.Type.UPGRADES, Bukkit.createInventory(null, 54, "Улучшения"));
        fillUpgrades(menu, player, data);
        show(player, menu);
    }

    public void openAchievements(Player player) {
        PlayerData data = players.get(player.getUniqueId());
        if (data == null) {
            return;
        }
        Menu menu = new Menu(Menu.Type.ACHIEVEMENTS, Bukkit.createInventory(null, 27, "Ачивменты"));
        fillAchievements(menu, data);
        show(player, menu);
    }

    public void openRebirth(Player player) {
        PlayerData data = players.get(player.getUniqueId());
        if (data == null) {
            return;
        }
        Menu menu = new Menu(Menu.Type.REBIRTH, Bukkit.createInventory(null, 9, "§8§lПерерождение"));
        fillRebirth(menu, data);
        show(player, menu);
    }

    public void openInfo(Player viewer, Player target) {
        PlayerData data = players.get(target.getUniqueId());
        if (data == null) {
            viewer.sendMessage("§cДанные игрока ещё не загружены!");
            return;
        }
        long now = System.currentTimeMillis();
        Menu menu = new Menu(Menu.Type.INFO, Bukkit.createInventory(null, 27,
                "§5" + target.getDisplayName() + "§f - " + (target.isOnline() ? "§2Онлайн" : "§4Оффлайн")));
        menu.inventory().setItem(14, items.barrier());
        menu.inventory().setItem(13, items.infoHead(target, data, now));
        menu.inventory().setItem(12, items.upgradesSummary(data));
        show(viewer, menu);
    }

    private void show(Player player, Menu menu) {
        player.openInventory(menu.inventory()); 
        open.put(player.getUniqueId(), menu);
    }

    

    
    public void refresh(Player player) {
        Menu menu = open.get(player.getUniqueId());
        PlayerData data = players.get(player.getUniqueId());
        if (menu == null || data == null || player.getOpenInventory().getTopInventory() != menu.inventory()) {
            return;
        }
        switch (menu.type()) {
            case UPGRADES:
                fillUpgrades(menu, player, data);
                break;
            case ACHIEVEMENTS:
                fillAchievements(menu, data);
                break;
            case REBIRTH:
                fillRebirth(menu, data);
                break;
            default:
                break;
        }
    }

    private void fillUpgrades(Menu menu, Player player, PlayerData data) {
        Inventory inv = menu.inventory();
        inv.clear();
        menu.clearSlots();
        for (UpgradeType type : UpgradeType.values()) {
            UpgradeType req = type.requires();
            boolean unlocked = req == null
                    || data.getUpgrade(req) > 0
                    || data.getCookies() >= req.price(data.getUpgrade(req));
            if (unlocked) {
                inv.setItem(type.menuSlot(), items.upgradeItem(player, data, type));
                menu.setUpgrade(type.menuSlot(), type);
            } else {
                inv.setItem(type.menuSlot(), items.lockedItem(data, type));
            }
        }
    }

    private void fillAchievements(Menu menu, PlayerData data) {
        Inventory inv = menu.inventory();
        inv.clear();
        menu.clearSlots();
        List<Achievement> list = achievements.available(data);
        for (int i = 0; i < Math.min(list.size(), ACHIEVEMENT_SLOTS); i++) {
            inv.setItem(i, items.achievementItem(list.get(i)));
            menu.setAchievement(i, list.get(i).id());
        }
    }

    private void fillRebirth(Menu menu, PlayerData data) {
        menu.inventory().setItem(4, items.rebirthItem(data, System.currentTimeMillis()));
    }

    

    
    public Menu menuOf(Player player, Inventory topInventory) {
        Menu menu = open.get(player.getUniqueId());
        return menu != null && menu.inventory() == topInventory ? menu : null;
    }

    public void onClosed(Player player, Inventory inventory) {
        Menu menu = open.get(player.getUniqueId());
        if (menu != null && menu.inventory() == inventory) {
            open.remove(player.getUniqueId());
        }
    }

    public void forget(Player player) {
        open.remove(player.getUniqueId());
    }
}
