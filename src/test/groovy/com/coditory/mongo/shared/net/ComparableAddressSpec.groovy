package com.coditory.mongo.shared.net

import spock.lang.Specification

class ComparableAddressSpec extends Specification {
    def "should create comparable address string from: #ip"() {
        given:
            InetAddress address = InetAddress.getByName(ip)
        when:
            String comparableIp = ComparableAddress.toComparableStringAddress(address)
        then:
            comparableIp == expected
        where:
            ip                                        | expected
            "127.0.0.1"                               | "0000000000000000000000000000002130706433"
            "127.0.0.2"                               | "0000000000000000000000000000002130706434"
            "127.0.0.126"                             | "0000000000000000000000000000002130706558"
            "127.0.0.127"                             | "0000000000000000000000000000002130706559"
            "127.0.1.0"                               | "0000000000000000000000000000002130706688"
            "0.0.0.1"                                 | "0000000000000000000000000000000000000001"
            "127.127.127.127"                         | "0000000000000000000000000000002139062143"
            "0:0:0:0:0:ffff:7f00:1"                   | "0000000000000000000000000000002130706433"
            "0:0:0:0:0:ffff:7f00:2"                   | "0000000000000000000000000000002130706434"
            "0:0:0:0:0:ffff:7f00:7e"                  | "0000000000000000000000000000002130706558"
            "0:0:0:0:0:ffff:7f00:7f"                  | "0000000000000000000000000000002130706559"
            "0:0:0:0:0:ffff:7f00:100"                 | "0000000000000000000000000000002130706688"
            "0:0:0:0:0:0:0:1"                         | "0000000000000000000000000000000000000001"
            "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff" | "0340282366920938463463374607431768211455"
    }
}
