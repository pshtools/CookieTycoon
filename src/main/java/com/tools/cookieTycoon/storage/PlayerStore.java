package com.tools.cookieTycoon.storage;

import org.bson.Document;

import java.util.Collection;
import java.util.UUID;


public interface PlayerStore extends AutoCloseable {

    
    Document find(UUID uuid);

    
    void saveBatch(Collection<Document> documents);

    @Override
    void close();
}
