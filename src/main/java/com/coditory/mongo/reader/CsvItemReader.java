package com.coditory.mongo.reader;

import com.coditory.mongo.shared.Item;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

class CsvItemReader implements FormatSpecificItemReader {
    @Override
    public Flux<Item> read(File file) {
        return Flux.defer(() -> Flux.fromStream(lines(file)))
            .next()
            .map(CsvParser::parseCsvRow)
            .flatMapMany(titleRow -> getItems(file, titleRow));
    }

    private Flux<Item> getItems(File file, List<String> titleRow) {
        return Flux.defer(() -> Flux.fromStream(lines(file)))
            .skip(1)
            .map(CsvParser::parseCsvRow)
            .map(row -> rowToItem(titleRow, row));
    }

    private Stream<String> lines(File file) {
        try {
            return Files.lines(file.toPath());
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read file: " + file.getAbsolutePath(), e);
        }
    }

    private Item rowToItem(List<String> titleRow, List<String> row) {
        Item.ItemBuilder builder = Item.builder();
        for (int i = 0; i < titleRow.size() && i < row.size(); ++i) {
            builder.addAndParseValue(titleRow.get(i), row.get(i));
        }
        return builder.build();
    }
}
