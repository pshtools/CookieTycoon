package com.tools.cookieTycoon.data;

import com.tools.cookieTycoon.model.PlayerData;
import com.tools.cookieTycoon.model.UpgradeType;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public final class PlayerCodec {

    private PlayerCodec() {
    }

    public static Document encode(PlayerData data, long nowMs) {
        Document upgrades = new Document();
        for (UpgradeType type : UpgradeType.values()) {
            upgrades.put(type.key(), data.getUpgrade(type));
        }

        List<Integer> achievements = new ArrayList<>();
        for (int id : data.achievementIds()) {
            achievements.add(id);
        }

        Document document = new Document();
        document.put("uuid", data.uuid().toString());
        document.put("cookies", data.getCookies());
        document.put("allcookies", data.getAllCookies());
        document.put("rebirth", data.getRebirth());
        document.put("upgrades", upgrades);
        document.put("achievements", achievements);
        document.put("playtime", data.totalPlayTimeMs(nowMs));
        document.put("playtime_rebirth", data.rebirthPlayTimeMs(nowMs));
        return document;
    }

    
    public static PlayerData decode(UUID uuid, Document document, LegacyData legacy) {
        PlayerData data = new PlayerData(uuid);
        boolean imported = false;

        if (document != null) {
            data.setCookies(number(document.get("cookies")));
            data.setAllCookies(number(document.get("allcookies")));
            data.setRebirth((int) number(document.get("rebirth")));

            Object upgrades = document.get("upgrades");
            if (upgrades instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) upgrades;
                for (UpgradeType type : UpgradeType.values()) {
                    Object value = map.get(type.key());
                    if (value instanceof Number) {
                        data.setUpgrade(type, ((Number) value).intValue());
                    }
                }
            }
        }

        Object achievements = document != null ? document.get("achievements") : null;
        if (achievements instanceof Collection) {
            for (Object id : (Collection<?>) achievements) {
                if (id instanceof Number) {
                    data.addAchievement(((Number) id).intValue());
                }
            }
        } else {
            int[] old = legacy.achievements(uuid);
            if (old != null) {
                for (int id : old) {
                    data.addAchievement(id);
                }
                imported = true;
            }
        }

        if (document != null && document.get("playtime") instanceof Number) {
            data.loadPlayTime((long) number(document.get("playtime")),
                    (long) number(document.get("playtime_rebirth")));
        } else {
            long[] old = legacy.playTime(uuid);
            if (old != null) {
                data.loadPlayTime(old[0], old[1]);
                imported = true;
            }
        }

        if (imported) {
            data.markDirty();
        } else {
            data.clearDirty();
        }
        return data;
    }

    private static double number(Object value) {
        return value instanceof Number ? ((Number) value).doubleValue() : 0.0;
    }
}
