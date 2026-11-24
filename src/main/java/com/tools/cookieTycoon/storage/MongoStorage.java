package com.tools.cookieTycoon.storage;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class MongoStorage implements PlayerStore {

    private static final UpdateOptions UPSERT = new UpdateOptions().upsert(true);
    private static final BulkWriteOptions UNORDERED = new BulkWriteOptions().ordered(false);

    private final MongoClient client;
    private final MongoCollection<Document> players;

    public MongoStorage(String connectionString, String databaseName) {

        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);

        MongoClientSettings settings = MongoClientSettings.builder()


                .applyToClusterSettings(b -> b.serverSelectionTimeout(5, TimeUnit.SECONDS))
                .applyToSocketSettings(b -> b.connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS))
                .applyConnectionString(new ConnectionString(connectionString))
                .build();

        this.client = MongoClients.create(settings);
        this.players = client.getDatabase(databaseName).getCollection("players");
        this.players.createIndex(Indexes.ascending("uuid"), new IndexOptions().unique(true));
    }

    @Override
    public Document find(UUID uuid) {
        return players.find(Filters.eq("uuid", uuid.toString())).first();
    }

    @Override
    public void saveBatch(Collection<Document> documents) {
        if (documents.isEmpty()) {
            return;
        }
        List<WriteModel<Document>> operations = new ArrayList<>(documents.size());
        for (Document document : documents) {
            operations.add(new UpdateOneModel<Document>(
                    Filters.eq("uuid", document.getString("uuid")),
                    new Document("$set", document),
                    UPSERT));
        }
        players.bulkWrite(operations, UNORDERED);
    }

    @Override
    public void close() {
        client.close();
    }
}
