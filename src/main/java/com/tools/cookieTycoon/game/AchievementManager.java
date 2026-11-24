package com.tools.cookieTycoon.game;

import com.tools.cookieTycoon.data.PlayerDataManager;
import com.tools.cookieTycoon.model.Achievement;
import com.tools.cookieTycoon.model.Achievements;
import com.tools.cookieTycoon.model.PlayerData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public final class AchievementManager {

    private final PlayerDataManager players;

    public AchievementManager(PlayerDataManager players) {
        this.players = players;
    }

    
    public List<Achievement> available(PlayerData data) {
        List<Achievement> result = new ArrayList<>();
        for (Achievement a : Achievements.all()) {
            if (!data.hasAchievement(a.id()) && data.getUpgrade(a.type()) >= a.requiredCount()) {
                result.add(a);
            }
        }
        result.sort(Comparator.comparingDouble(Achievement::price));
        return result;
    }

    
    public boolean buy(Player player, int id) {
        Achievement achievement = Achievements.get(id);
        if (achievement == null) {
            return false;
        }
        PlayerData data = players.require(player.getUniqueId());

        if (data.hasAchievement(id)) {
            player.sendMessage("У вас уже есть");
            return false;
        }
        if (data.getUpgrade(achievement.type()) < achievement.requiredCount()) {
            player.sendMessage("§cВы ещё не выполнили условие этой ачивки");
            return false;
        }
        if (data.getCookies() < achievement.price()) {
            player.sendMessage("§cВам не хватает");
            return false;
        }

        data.spend(achievement.price());
        data.addAchievement(id);
        player.sendMessage("§aВы успешно купили §r§l" + achievement.name());
        return true;
    }
}
