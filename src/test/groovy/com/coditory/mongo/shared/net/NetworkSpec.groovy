package com.coditory.mongo.shared.net

import com.coditory.mongo.shared.net.Network
import spock.lang.Specification
import spock.lang.Unroll

class NetworkSpec extends Specification {
    @Unroll
    def "should resolve network and broadcast addresses for: #cidr"() {
        when:
            Network network = Network.create(cidr)
        then:
            network.networkAddress.getHostAddress() == networkAddress
            network.broadcastAddress.getHostAddress() == broadcastAddress
        where:
            cidr                 | networkAddress        | broadcastAddress
            "10.77.12.11/18"     | "10.77.0.0"           | "10.77.63.255"
            "435:23f::45:23/101" | "435:23f:0:0:0:0:0:0" | "435:23f:0:0:0:0:7ff:ffff"
    }
}
