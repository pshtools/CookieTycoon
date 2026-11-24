package com.tools.cookieTycoon.data;

import com.tools.cookieTycoon.model.PlayerData;
import com.tools.cookieTycoon.storage.PlayerStore;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class PlayerDataManager {

    private static final long IO_TIMEOUT_SECONDS = 10;
    private static final long STALE_NANOS = TimeUnit.MINUTES.toNanos(2);
    private static final long FAILURE_LOG_INTERVAL_NANOS = TimeUnit.SECONDS.toNanos(30);

    private final Logger log;
    private final PlayerStore storage;
    private final LegacyData legacy;
    private final ExecutorService io;

    
    private final Map<UUID, PlayerData> online = new ConcurrentHashMap<>();
    
    private final Map<UUID, Preloaded> preloaded = new ConcurrentHashMap<>();
    
    private final Map<UUID, Document> pendingWrites = new ConcurrentHashMap<>();
    
    private final Map<UUID, Long> unloadedAt = new ConcurrentHashMap<>();

    private volatile long lastFailureLogNanos;

    private static final class Preloaded {
        final PlayerData data;
        final long startedAtNanos;

        Preloaded(PlayerData data, long startedAtNanos) {
            this.data = data;
            this.startedAtNanos = startedAtNanos;
        }
    }

    public PlayerDataManager(Logger log, PlayerStore storage, LegacyData legacy) {
        this.log = log;
        this.storage = storage;
        this.legacy = legacy;
        this.io = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "CookieTycoon-IO");
            thread.setDaemon(true);
            return thread;
        });
    }



    
    public void preload(UUID uuid) throws Exception {
        long started = System.nanoTime();
        PlayerData data = io.submit(() -> read(uuid)).get(IO_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        preloaded.put(uuid, new Preloaded(data, started));
    }

    
    public PlayerData activate(UUID uuid, String name) {

        PlayerData data = null;
        Preloaded pre = preloaded.remove(uuid);
        if (pre != null) {


            Long unloaded = unloadedAt.get(uuid);
            boolean stale = unloaded != null && unloaded - pre.startedAtNanos > 0;
            if (!stale) {
                data = pre.data;
            }
        }
        if (data == null) {
            try {
                data = io.submit(() -> read(uuid)).get(IO_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Не удалось загрузить данные игрока " + name, e);
                return null;
            }
        }

        data.startSession(System.currentTimeMillis());
        data.lastAccrualNanos(System.nanoTime());
        online.put(uuid, data);
        return data;
    }

    
    public void unload(UUID uuid) {
        preloaded.remove(uuid);

        PlayerData data = online.remove(uuid);
        if (data == null) {
            return;
        }

        enqueue(List.of(PlayerCodec.encode(data, System.currentTimeMillis())));
        data.clearDirty();
        unloadedAt.put(uuid, System.nanoTime());
    }



    
    public PlayerData get(UUID uuid) {
        return online.get(uuid);
    }

    
    public PlayerData require(UUID uuid) {
        PlayerData data = online.get(uuid);
        if (data == null) {
            throw new IllegalStateException("Данные игрока " + uuid + " не загружены");
        }
        return data;
    }

    public Collection<PlayerData> online() {
        return online.values();
    }



    
    public void autosave() {
        long now = System.currentTimeMillis();
        List<Document> documents = new ArrayList<>();
        for (PlayerData data : online.values()) {
            if (data.isDirty()) {
                documents.add(PlayerCodec.encode(data, now));
                data.clearDirty();
            }
        }

        if (!documents.isEmpty()) {
            enqueue(documents);
        } else if (!pendingWrites.isEmpty()) {
            io.execute(this::drain);
        }
        purgeStale();
    }

    
    public void shutdown() {
        long now = System.currentTimeMillis();
        for (Map.Entry<UUID, PlayerData> entry : online.entrySet()) {
            pendingWrites.put(entry.getKey(), PlayerCodec.encode(entry.getValue(), now));
        }
        online.clear();

        io.execute(this::drain);
        io.shutdown();
        try {
            if (!io.awaitTermination(30, TimeUnit.SECONDS)) {
                log.severe("Запись данных игроков не уложилась в 30 секунд");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (!pendingWrites.isEmpty()) {
            drain();
        }
        if (!pendingWrites.isEmpty()) {
            log.severe("НЕ СОХРАНЕНЫ данные " + pendingWrites.size() + " игроков: БД недоступна!");
        }
    }



    
    void awaitIo() throws Exception {
        io.submit(() -> { }).get(IO_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    
    private PlayerData read(UUID uuid) {
        Document document = pendingWrites.get(uuid);
        if (document == null) {
            document = storage.find(uuid);
        }
        return PlayerCodec.decode(uuid, document, legacy);
    }

    private void enqueue(List<Document> documents) {
        for (Document document : documents) {
            pendingWrites.put(UUID.fromString(document.getString("uuid")), document);
        }
        io.execute(this::drain);
    }

    
    private void drain() {
        if (pendingWrites.isEmpty()) {
            return;
        }
        Map<UUID, Document> batch = new HashMap<>(pendingWrites);
        try {
            storage.saveBatch(batch.values());
            for (Map.Entry<UUID, Document> entry : batch.entrySet()) {

                pendingWrites.remove(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            long now = System.nanoTime();
            if (now - lastFailureLogNanos > FAILURE_LOG_INTERVAL_NANOS) {
                lastFailureLogNanos = now;
                log.log(Level.WARNING, "Не удалось записать данные " + batch.size()
                        + " игроков в MongoDB, повторю при следующем автосохранении", e);
            }
        }
    }

    private void purgeStale() {
        long now = System.nanoTime();
        preloaded.values().removeIf(p -> now - p.startedAtNanos > STALE_NANOS);
        unloadedAt.values().removeIf(t -> now - t > STALE_NANOS);
    }
}
