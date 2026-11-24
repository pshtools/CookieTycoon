package com.tools.cookieTycoon.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class LegacyData {

    public static final LegacyData EMPTY = new LegacyData(Collections.emptyMap(), Collections.emptyMap());

    private final Map<UUID, int[]> achievements;
    private final Map<UUID, long[]> playTime;

    private LegacyData(Map<UUID, int[]> achievements, Map<UUID, long[]> playTime) {
        this.achievements = achievements;
        this.playTime = playTime;
    }

    
    private static final class LegacyAchievements {
        List<Integer> IDs;
    }

    private static final class LegacyPlayTime {
        long totalPlayTime;
        long currentRebirthPlayTime;
    }

    public static LegacyData load(File dataFolder, Logger log) {
        Map<UUID, LegacyAchievements> ach = read(new File(dataFolder, "achievements.json"), log,
                new TypeToken<Map<UUID, LegacyAchievements>>() { }.getType());
        Map<UUID, LegacyPlayTime> time = read(new File(dataFolder, "time.json"), log,
                new TypeToken<Map<UUID, LegacyPlayTime>>() { }.getType());

        Map<UUID, int[]> achievements = new HashMap<>();
        for (Map.Entry<UUID, LegacyAchievements> e : ach.entrySet()) {
            if (e.getKey() == null || e.getValue() == null || e.getValue().IDs == null) {
                continue;
            }
            achievements.put(e.getKey(), e.getValue().IDs.stream()
                    .filter(id -> id != null && id >= 0).mapToInt(Integer::intValue).toArray());
        }

        Map<UUID, long[]> playTime = new HashMap<>();
        for (Map.Entry<UUID, LegacyPlayTime> e : time.entrySet()) {
            if (e.getKey() != null && e.getValue() != null) {
                playTime.put(e.getKey(), new long[]{e.getValue().totalPlayTime, e.getValue().currentRebirthPlayTime});
            }
        }

        if (!achievements.isEmpty() || !playTime.isEmpty()) {
            log.info("Найдены старые данные (ачивки: " + achievements.size()
                    + ", время в игре: " + playTime.size() + "). Они будут перенесены в MongoDB при входе игроков.");
        }
        return new LegacyData(achievements, playTime);
    }

    private static <T> Map<UUID, T> read(File file, Logger log, Type type) {
        if (!file.isFile()) {
            return Collections.emptyMap();
        }
        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            Map<UUID, T> map = new Gson().fromJson(reader, type);
            return map != null ? map : Collections.emptyMap();
        } catch (IOException | RuntimeException e) {
            log.log(Level.WARNING, "Не удалось прочитать " + file.getName() + " — старые данные будут пропущены", e);
            return Collections.emptyMap();
        }
    }

    
    public int[] achievements(UUID uuid) {
        return achievements.get(uuid);
    }

    
    public long[] playTime(UUID uuid) {
        return playTime.get(uuid);
    }
}
