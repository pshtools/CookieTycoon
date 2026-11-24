package com.tools.cookieTycoon.game;

import com.tools.cookieTycoon.ui.ItemFactory;
import com.tools.cookieTycoon.ui.PlayerHud;
import com.tools.cookieTycoon.model.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public final class GameLoop {

    private static final int BUCKETS = 20;
    private static final int ACTION_BAR_PERIOD = 5;

    private final Plugin plugin;
    private final CookieManager cookies;
    private final ItemFactory items;

    private final List<List<PlayerHud>> buckets = new ArrayList<>();
    private final Map<UUID, PlayerHud> huds = new HashMap<>();

    private BukkitTask task;
    private int tick;

    public GameLoop(Plugin plugin, CookieManager cookies, ItemFactory items) {
        this.plugin = plugin;
        this.cookies = cookies;
        this.items = items;
        for (int i = 0; i < BUCKETS; i++) {
            buckets.add(new ArrayList<>());
        }
    }

    public void start() {
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::run, 1L, 1L);
    }

    public void attach(Player player, PlayerData data) {
        detach(player);
        PlayerHud hud = new PlayerHud(player, data, items);
        hud.attach();
        huds.put(player.getUniqueId(), hud);
        smallestBucket().add(hud);
    }

    
    public void detach(Player player) {
        PlayerHud hud = huds.remove(player.getUniqueId());
        if (hud != null) {
            for (List<PlayerHud> bucket : buckets) {
                if (bucket.remove(hud)) {
                    break;
                }
            }
        }
    }

    public void shutdown() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        for (PlayerHud hud : huds.values()) {
            hud.detach();
        }
        huds.clear();
        buckets.forEach(List::clear);
    }

    private List<PlayerHud> smallestBucket() {
        List<PlayerHud> smallest = buckets.get(0);
        for (List<PlayerHud> bucket : buckets) {
            if (bucket.size() < smallest.size()) {
                smallest = bucket;
            }
        }
        return smallest;
    }

    private void run() {
        int t = tick++;
        long now = System.nanoTime();

        for (PlayerHud hud : buckets.get(t % BUCKETS)) {
            cookies.accrue(hud.data(), now);
            hud.updateSidebar();
            hud.updateHotbar();
        }

        for (int b = t % ACTION_BAR_PERIOD; b < BUCKETS; b += ACTION_BAR_PERIOD) {
            for (PlayerHud hud : buckets.get(b)) {
                hud.updateActionBar();
            }
        }
    }
}
