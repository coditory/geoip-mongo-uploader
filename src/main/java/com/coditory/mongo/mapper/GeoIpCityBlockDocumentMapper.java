package com.coditory.mongo.mapper;

import com.coditory.mongo.shared.Item;
import com.coditory.mongo.shared.net.Network;
import lombok.AllArgsConstructor;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.coditory.mongo.shared.net.ComparableAddress.toComparableStringAddress;

@AllArgsConstructor
public class GeoIpCityBlockDocumentMapper implements DocumentMapper {
    private final Set<String> skip = Set.of(
            "isSatelliteProvider", "postalCode", "isAnonymousProxy", "registeredCountryGeonameId");

    @Override
    public Document toDocument(Item item) {
        Map<String, Object> fields = new HashMap<>(item.getFields());
        Network network = Network.create((String) fields.get("network"));
        fields.put("prefix", network.getPrefixLength());
        fields.put("networkAddressDec", toComparableStringAddress(network.getNetworkAddress()));
        fields.put("broadcastAddressDec", toComparableStringAddress(network.getBroadcastAddress()));
        fields.put("_id", fields.get("network"));
        fields.remove("network");
        skip.forEach(fields::remove);
        return new Document(fields);
    }
}
