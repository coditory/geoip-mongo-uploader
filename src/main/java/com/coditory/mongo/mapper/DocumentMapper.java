package com.coditory.mongo.mapper;

import com.coditory.mongo.shared.Item;
import org.bson.Document;

public interface DocumentMapper {
    Document toDocument(Item item);
}
