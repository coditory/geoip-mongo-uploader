package com.coditory.mongo.shared;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.AllArgsConstructor;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static com.coditory.mongo.shared.Logger.stdout;
import static com.mongodb.reactivestreams.client.MongoClients.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@AllArgsConstructor
public class MongoUri {
    private final String mongoUri;

    public MongoDatabase getMongoDatabase(MongoClient mongoClient) {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        return mongoClient.getDatabase(connectionString.getDatabase());
    }

    public MongoClient createMongoClient() {
        stdout("Connecting with mongo: " + mongoUri);
        ConnectionString connectionString = new ConnectionString(mongoUri);
        PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
        return MongoClients.create(settings);
    }
}
