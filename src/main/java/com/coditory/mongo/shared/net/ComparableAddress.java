package com.coditory.mongo.shared.net;

import java.math.BigInteger;
import java.net.InetAddress;

public class ComparableAddress {
    public static String toComparableStringAddress(InetAddress inetAddress) {
        BigInteger number = new BigInteger(1, inetAddress.getAddress());
        StringBuilder textBuilder = new StringBuilder(number.toString());
        while (textBuilder.length() < 40) {
            textBuilder.insert(0, "0");
        }
        return textBuilder.toString();
    }
}
