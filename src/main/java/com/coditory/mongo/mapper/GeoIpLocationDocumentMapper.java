package com.coditory.mongo.mapper;

import com.coditory.mongo.shared.Item;
import lombok.AllArgsConstructor;
import org.bson.Document;

import java.util.Set;

@AllArgsConstructor
public class GeoIpLocationDocumentMapper implements DocumentMapper {
    private final String idField = "geonameId";
    private final Set<String> skip = Set.of(
            "continentName", "countryName", "localeCode", "metroCode", "isInEuropeanUnion", "subdivision1IsoCode", "subdivision1Name", "subdivision2IsoCode", "subdivision2Name");

    @Override
    public Document toDocument(Item item) {
        Document document = new Document(item.getFields());
        if (document.containsKey(idField)) {
            document.append("_id", document.get(idField));
            document.remove(idField);
        }
        skip.forEach(document::remove);
        return document;
    }
}
