package com.coditory.mongo.reader;

import com.coditory.mongo.shared.Item;
import reactor.core.publisher.Flux;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ItemReader {
    private static final Map<String, FormatSpecificItemReader> readerBySuffix = new HashMap<>();

    static {
        readerBySuffix.put(".csv", new CsvItemReader());
    }

    public Flux<Item> readItems(Path filePath) {
        return readFile(filePath.toFile());
    }

    private Flux<Item> readFile(File file) {
        if (!file.isFile()) {
            return Flux.error(new IllegalArgumentException("Could not open file: " + file.getAbsolutePath()));
        }
        return readerBySuffix.keySet().stream()
            .filter(readerBySuffix::containsKey)
            .map(readerBySuffix::get)
            .findFirst()
            .map(reader -> reader.read(file))
            .orElseThrow(() ->
                new IllegalArgumentException("Not supported file format: " + file.getAbsolutePath()
                    + ". Supported formats: " + readerBySuffix.keySet())
            );
    }
}
