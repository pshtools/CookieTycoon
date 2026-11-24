package com.tools.cookieTycoon.ui;

import com.tools.cookieTycoon.game.RebirthManager;
import com.tools.cookieTycoon.model.PlayerData;
import com.tools.cookieTycoon.model.UpgradeType;
import com.tools.cookieTycoon.utils.NumberFormatter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class PlayerHud {

    private static final int LINES = 5;
    private static final int FIRST_ITEM_SLOT = 9;

    private static final String[] TEAM_NAMES = {"ct_line_0", "ct_line_1", "ct_line_2", "ct_line_3", "ct_line_4"};
    private static final String[] ENTRIES = {
            "§0", "§1", "§2", "§3", "§4" 
    };

    private final Player player;
    private final PlayerData data;
    private final ItemFactory items;

    private String lastActionBar;
    private final String[] lastLines = new String[LINES];
    private int renderedRevision = -1;

    private Scoreboard previousBoard;
    private Team[] teams;

    public PlayerHud(Player player, PlayerData data, ItemFactory items) {
        this.player = player;
        this.data = data;
        this.items = items;
    }

    public Player player() {
        return player;
    }

    public PlayerData data() {
        return data;
    }

    

    public void attach() {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("cookietycoon", "dummy", "§6§lCOOKIE TYCOON");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        teams = new Team[LINES];
        for (int i = 0; i < LINES; i++) {
            Team team = board.registerNewTeam(TEAM_NAMES[i]);
            team.addEntry(ENTRIES[i]);
            objective.getScore(ENTRIES[i]).setScore(LINES - i);
            teams[i] = team;
        }

        previousBoard = player.getScoreboard();
        player.setScoreboard(board);

        updateSidebar();
        updateHotbar();
        updateActionBar();
    }

    
    public void detach() {
        if (!player.isOnline()) {
            return;
        }
        if (previousBoard != null) {
            player.setScoreboard(previousBoard);
        }
        sendActionBar("");
    }

    

    public void updateActionBar() {
        String message = "§6§l" + NumberFormatter.format(data.getCookies()) + " ⃝";
        if (message.equals(lastActionBar)) {
            return;
        }
        lastActionBar = message;
        sendActionBar(message);
    }

    private void sendActionBar(String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public void updateSidebar() {
        if (teams == null) {
            return;
        }
        double allCookies = data.getAllCookies();
        double rebirthPrice = RebirthManager.price(data.getRebirth());

        String[] lines = {
                " ",
                "§fCPS: §6" + NumberFormatter.format(data.stats().cps()),
                "§fВсего: §6" + NumberFormatter.format(allCookies),
                "§fПрестиж: §6" + NumberFormatter.format(data.getRebirth()),
                allCookies >= rebirthPrice
                        ? "§aВозвышение доступно!"
                        : "§fДо возвышения: §6" + NumberFormatter.format(Math.max(0, rebirthPrice - allCookies))
        };

        for (int i = 0; i < LINES; i++) {
            if (!lines[i].equals(lastLines[i])) {
                lastLines[i] = lines[i];
                setTeamText(teams[i], lines[i]);
            }
        }
    }

    
    public void updateHotbar() {
        if (renderedRevision == data.revision()) {
            return;
        }
        renderedRevision = data.revision();

        for (UpgradeType type : UpgradeType.values()) {
            int slot = FIRST_ITEM_SLOT + type.ordinal();
            player.getInventory().setItem(slot,
                    data.getUpgrade(type) != 0 ? items.hotbarItem(player, data, type) : null);
        }
    }

    
    private static void setTeamText(Team team, String text) {
        int split = Math.min(16, text.length());
        if (split > 0 && text.charAt(split - 1) == '§') {
            split--;
        }

        String prefix = text.substring(0, split);
        String suffix = "";
        if (split < text.length()) {
            suffix = org.bukkit.ChatColor.getLastColors(prefix) + text.substring(split);
            if (suffix.length() > 16) {
                suffix = suffix.substring(0, 16);
            }
        }
        team.setPrefix(prefix);
        team.setSuffix(suffix);
    }
}
