package com.tools.cookieTycoon.ui;

import com.tools.cookieTycoon.model.Achievement;
import com.tools.cookieTycoon.model.PlayerData;
import com.tools.cookieTycoon.model.PlayerStats;
import com.tools.cookieTycoon.model.UpgradeType;
import com.tools.cookieTycoon.game.RebirthManager;
import com.tools.cookieTycoon.utils.NumberFormatter;
import com.tools.cookieTycoon.utils.TimeFormatter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;


public final class ItemFactory {

    private static final double BULK_10 = UpgradeType.bulkFactor(10);
    private static final double BULK_100 = UpgradeType.bulkFactor(100);

    private final Map<UpgradeType, Material> icons = new EnumMap<>(UpgradeType.class);

    
    public ItemFactory() {
        for (UpgradeType type : UpgradeType.values()) {
            icons.put(type, Material.valueOf(type.icon()));
        }
    }

    

    private static String label(UpgradeType type) {
        return "§l" + type.displayName();
    }

    private static String title(UpgradeType type, Player player) {
        return label(type) + (type.isPersonal() ? player.getDisplayName() : "");
    }

    private static void addBuyLines(List<String> lore, double price) {
        lore.add("§7(ЛКМ) Купить x1 - " + NumberFormatter.format(price));
        lore.add("§7(ПКМ) Купить x10 - " + NumberFormatter.format(price * BULK_10));
        lore.add("§7(СКМ) Купить x100 - " + NumberFormatter.format(price * BULK_100));
    }

    
    public ItemStack upgradeItem(Player player, PlayerData data, UpgradeType type) {
        List<String> lore = new ArrayList<>();
        for (String line : type.lore()) {
            lore.add("§e" + line);
        }
        lore.add("");
        addBuyLines(lore, type.price(data.getUpgrade(type)));
        return build(icons.get(type), title(type, player), lore);
    }

    
    public ItemStack lockedItem(PlayerData data, UpgradeType type) {
        List<String> lore = new ArrayList<>();
        lore.add("§8???");
        lore.add("");
        addBuyLines(lore, type.price(data.getUpgrade(type)));
        return build(Material.BARRIER, "§8???", lore);
    }

    
    public ItemStack hotbarItem(Player player, PlayerData data, UpgradeType type) {
        PlayerStats stats = data.stats();
        int count = data.getUpgrade(type);
        double total = stats.cpsOf(type);
        double perUnit = count > 0 ? total / count : 0;
        String name = label(type).toLowerCase();
        String owner = type.isPersonal() ? player.getDisplayName() : "";

        List<String> lore = new ArrayList<>();
        lore.add("§e" + type.lore().get(0));
        lore.add("");
        if (type.isPersonal()) {
            lore.add("§7• каждый " + name + owner + "§7 производит §f" + NumberFormatter.format(perUnit)
                    + " печенья §7в секунду");
        } else {
            double share = stats.cps() > 0 ? total / stats.cps() : 0;
            lore.add("§7• каждый " + name + "§7 производит §f" + NumberFormatter.format(perUnit)
                    + " печенья §7в секунду (§f" + NumberFormatter.formatPercent(share) + "§7 от");
            lore.add("§7  общего значения печ/с)");
        }
        lore.add("§7• " + NumberFormatter.format(count) + " " + name + owner + "§r§7 производят §f"
                + NumberFormatter.format(total) + " печенья §7в секунду");
        lore.add("§7• §fпеченья N §7изготовлено на данный момент");

        return build(icons.get(type),
                title(type, player) + " §7(куплено: " + NumberFormatter.format(count) + ")", lore);
    }

    

    public ItemStack achievementItem(Achievement a) {
        List<String> lore = new ArrayList<>();
        String plural = a.type().pluralName();
        if (a.isMultiplicative()) {
            lore.add("§7" + plural + " становятся §fв " + NumberFormatter.format(a.bonus()) + " раза §7эффективнее");
        } else {
            lore.add("§7" + plural + "§7 получают §f+" + NumberFormatter.format(a.bonus())
                    + "§7 печенек за каждое");
            lore.add("§7строение, не являющееся " + a.type().key());
        }
        lore.add("");
        lore.add("§6⃝ §a" + NumberFormatter.format(a.price()));
        return build(Material.EMERALD, "§r§l" + a.name(), lore);
    }

    

    public ItemStack rebirthItem(PlayerData data, long nowMs) {
        boolean available = RebirthManager.isAvailable(data);
        List<String> lore = new ArrayList<>();
        lore.add("§e" + data.getRebirth() + " §fуровень престижа");
        lore.add("§fВы пробыли в этой реальности: §e" + TimeFormatter.format(data.rebirthPlayTimeMs(nowMs)));
        lore.add("");
        lore.add(available ? "§aВам доступно перерождение!" : "§cВам недоступно перерождение");
        lore.add("§7" + NumberFormatter.format(data.getAllCookies()) + "/"
                + NumberFormatter.format(RebirthManager.price(data.getRebirth())));
        return build(available ? Material.ENCHANTED_GOLDEN_APPLE : Material.GOLDEN_APPLE,
                "§6Совершить перерождение", lore);
    }

    

    public ItemStack infoHead(Player target, PlayerData data, long nowMs) {
        List<String> lore = new ArrayList<>();
        lore.add("§6" + data.getRebirth() + "§f уровень престижа");
        lore.add("§fПеченья: §6" + NumberFormatter.format(data.getCookies()));
        lore.add("§fВ секунду: §6" + NumberFormatter.format(data.stats().cps()));
        lore.add("");
        lore.add("§fПеченья за всё время: §6" + NumberFormatter.format(data.getAllCookies()));
        lore.add("§fНаиграно всего: §6" + TimeFormatter.format(data.totalPlayTimeMs(nowMs)));
        lore.add("§fНаиграно с текущим рб: §6" + TimeFormatter.format(data.rebirthPlayTimeMs(nowMs)));
        return build(Material.PLAYER_HEAD, "§c" + "Информация о " + "§6" + target.getDisplayName(), lore);
    }

    public ItemStack upgradesSummary(PlayerData data) {
        String[] labels = {
                "Курсоры", "Бабули", "Фермы", "Шахты", "Фабрики", "Банки", "Храмы", "Башни",
                "Косм.доставка", "Алхим.лаб.", "Порталы", "Машины времени", "Конденсаторы", "Призмы",
                "Шансмейкеры", "Фракт.двигатели", "Консоли", "Вселенные", "Пекари", "You"
        };
        List<String> lore = new ArrayList<>();
        for (UpgradeType type : UpgradeType.values()) {
            lore.add("§f" + labels[type.ordinal()] + ": §6" + data.getUpgrade(type));
        }
        return build(Material.SUNFLOWER, "§6Улучшения", lore);
    }

    public ItemStack barrier() {
        return new ItemStack(Material.BARRIER);
    }

    private static ItemStack build(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
