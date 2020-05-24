package com.coditory.mongo.uploader;

import com.coditory.mongo.mapper.DocumentMapper;
import com.coditory.mongo.mapper.GeoIpCityBlockDocumentMapper;
import com.coditory.mongo.mapper.GeoIpLocationDocumentMapper;
import com.coditory.mongo.reader.ItemReader;
import com.coditory.mongo.shared.Arguments;
import com.coditory.mongo.shared.MongoUri;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

import static com.coditory.mongo.shared.Logger.stdout;

public class UploadCommand {
    public static void execute(Arguments arguments) {
        UploadArguments uploadArguments = UploadArguments.extract(arguments);
        MongoUri mongoUri = new MongoUri(uploadArguments.getMongoUrl());
        MongoClient mongoClient = mongoUri.createMongoClient();
        MongoUploader mongoUploader = MongoUploader.create(mongoClient, mongoUri);
        uploadGeoIpV4(mongoUploader, uploadArguments);
        uploadGeoIpV6(mongoUploader, uploadArguments);
        uploadGeoIpLocations(mongoUploader, uploadArguments);
        createIndexes(mongoClient, mongoUri);
        mongoClient.close();
    }

    private static void uploadGeoIpV4(MongoUploader mongoUploader, UploadArguments uploadArguments) {
        Path path = Path.of(uploadArguments.getDir(), "GeoLite2-City-Blocks-IPv4.csv");
        DocumentMapper mapper = new GeoIpCityBlockDocumentMapper();
        upload(mongoUploader, mapper, path, "geoIpV4");
    }

    private static void uploadGeoIpV6(MongoUploader mongoUploader, UploadArguments uploadArguments) {
        Path path = Path.of(uploadArguments.getDir(), "GeoLite2-City-Blocks-IPv6.csv");
        DocumentMapper mapper = new GeoIpCityBlockDocumentMapper();
        upload(mongoUploader, mapper, path, "geoIpV6");
    }

    private static void uploadGeoIpLocations(MongoUploader mongoUploader, UploadArguments uploadArguments) {
        Path path = Path.of(uploadArguments.getDir(), "GeoLite2-City-Locations-en.csv");
        DocumentMapper mapper = new GeoIpLocationDocumentMapper();
        upload(mongoUploader, mapper, path, "geoIpLocations");
    }

    private static void upload(MongoUploader mongoUploader, DocumentMapper documentMapper, Path filePath, String collection) {
        long start = System.currentTimeMillis();
        DataUploader dataUploader = new DataUploader(new ItemReader(), documentMapper, mongoUploader);
        Integer totalItems = dataUploader
                .upload(filePath, collection)
                .doOnNext(chunk -> stdout("Uploaded items %d in %d ms", chunk.getUploadedItems(), chunk.getMillis()))
                .reduce(0, (sum, chunk) -> sum + chunk.getUploadedItems())
                .block();
        stdout("Uploaded %d items in %d ms%n", totalItems, System.currentTimeMillis() - start);
    }

    private static void createIndexes(MongoClient mongoClient, MongoUri mongoUri) {
        MongoDatabase database = mongoUri.getMongoDatabase(mongoClient);
        long start = System.currentTimeMillis();
        createBlocksIndex(database.getCollection("geoIpV4"));
        createBlocksIndex(database.getCollection("geoIpV6"));
        stdout(">>> Created indexes in %d ms%n", System.currentTimeMillis() - start);
    }

    private static void createBlocksIndex(MongoCollection<Document> collection) {
        long start = System.currentTimeMillis();
        // Checked all possible index combinations
        // this one is sufficient
        Document index = new Document()
                .append("networkAddressDec", -1)
                .append("broadcastAddressDec", -1);
        Mono.from(collection.createIndex(index))
                .block();
        stdout("Created index for %s in %d ms", collection.getNamespace().getCollectionName(), System.currentTimeMillis() - start);
    }
}
