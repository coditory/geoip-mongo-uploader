package com.coditory.mongo.uploader;

import com.coditory.mongo.mapper.DocumentMapper;
import com.coditory.mongo.reader.ItemReader;
import com.coditory.mongo.uploader.MongoUploader.ChunkUploadResult;
import lombok.AllArgsConstructor;
import org.bson.Document;
import reactor.core.publisher.Flux;

import java.nio.file.Path;

@AllArgsConstructor
class DataUploader {
    ItemReader itemReader;
    DocumentMapper documentMapper;
    MongoUploader mongoUploader;

    Flux<ChunkUploadResult> upload(Path filePath, String collection) {
        Flux<Document> items = itemReader.readItems(filePath)
                .map(documentMapper::toDocument);
        return mongoUploader.upload(collection, items);
    }
}
