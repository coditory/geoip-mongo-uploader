package com.coditory.mongo.uploader;

import com.coditory.mongo.shared.MongoUri;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.bson.Document;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
class MongoUploader {
    static MongoUploader create(MongoClient mongoClient, MongoUri mongoUri) {
        MongoDatabase database = mongoUri.getMongoDatabase(mongoClient);
        return new MongoUploader(database);
    }

    @Value
    public static class ChunkUploadResult {
        int uploadedItems;
        long millis;
    }

    private final MongoDatabase database;

    Flux<ChunkUploadResult> upload(String collection, Flux<Document> items) {
        return Mono.from(database.getCollection(collection).drop())
                .thenMany(items)
                .buffer(5_000)
                .flatMap(docs -> uploadChunk(collection, docs));
    }

    private Mono<ChunkUploadResult> uploadChunk(String collection, List<Document> documents) {
        return Mono.defer(() -> {
            long start = System.currentTimeMillis();
            return Mono.from(database.getCollection(collection)
                    .insertMany(documents))
                    .map(result -> new ChunkUploadResult(documents.size(), System.currentTimeMillis() - start));
        });
    }
}
