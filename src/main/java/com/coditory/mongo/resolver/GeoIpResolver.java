package com.coditory.mongo.resolver;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.coditory.mongo.shared.net.ComparableAddress.toComparableStringAddress;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;

@AllArgsConstructor
public class GeoIpResolver {
    private final MongoDatabase mongoDatabase;

    Mono<Document> resolveGeoIp(String address) {
        InetAddress inetAddress = parse(address);
        return getGeoId(inetAddress)
                .flatMap(this::getGeoIpLocation);
    }

    private InetAddress parse(String address) {
        try {
            return InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Could not resolve address: " + address);
        }
    }

    private Mono<Integer> getGeoId(InetAddress address) {
        String cmpAddress = toComparableStringAddress(address);
        String collectionName = address.getAddress().length == 4
                ? "geoIpV4"
                : "geoIpV6";
        Bson query = and(
                lt("networkAddressDec", cmpAddress),
                gt("broadcastAddressDec", cmpAddress)
        );
        Bson sort = Sorts.descending("prefix");
        return Mono.from(mongoDatabase.getCollection(collectionName)
                .find(query).sort(sort))
                .map(doc -> doc.getInteger("geonameId"));
    }

    private Mono<Document> getGeoIpLocation(Integer id) {
        Bson query = Filters.eq("_id", id);
        return Mono.from(mongoDatabase.getCollection("geoIpLocations")
                .find(query));
    }
}
