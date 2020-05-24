package com.coditory.mongo.resolver;

import com.coditory.mongo.shared.Arguments;
import com.coditory.mongo.shared.MongoUri;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;

import static com.coditory.mongo.shared.Logger.stdout;

public class ResolveCommand {
    public static void execute(Arguments arguments) {
        ResolveArguments resolveArguments = ResolveArguments.extract(arguments);
        MongoUri mongoUri = new MongoUri(resolveArguments.getMongoUrl());
        MongoClient mongoClient = mongoUri.createMongoClient();
        MongoDatabase database = mongoUri.getMongoDatabase(mongoClient);
        GeoIpResolver geoIpResolver = new GeoIpResolver(database);
        resolve(geoIpResolver, resolveArguments.getResolve());
        mongoClient.close();
    }

    private static void resolve(GeoIpResolver geoIpResolver, String address) {
        long start = System.currentTimeMillis();
        Document document = geoIpResolver.resolveGeoIp(address)
                .block();
        stdout("Resolved location for %s:\n%s", address, document);
        stdout("Resolved in %d ms", System.currentTimeMillis() - start);
    }
}
