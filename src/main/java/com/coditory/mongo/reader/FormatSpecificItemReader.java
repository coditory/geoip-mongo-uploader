package com.coditory.mongo.reader;

import com.coditory.mongo.shared.Item;
import reactor.core.publisher.Flux;

import java.io.File;

interface FormatSpecificItemReader {
    Flux<Item> read(File file);
}
