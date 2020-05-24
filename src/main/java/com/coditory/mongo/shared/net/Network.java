package com.coditory.mongo.shared.net;

import lombok.Value;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Value
public class Network {
    public static Network create(String cidr) {
        return NetworkCreator.create(cidr);
    }

    String cidr;
    InetAddress inetAddress;
    int prefixLength;
    InetAddress networkAddress;
    InetAddress broadcastAddress;

    public InetAddress getNetworkAddress() {
        return this.networkAddress;
    }

    public InetAddress getBroadcastAddress() {
        return this.broadcastAddress;
    }

    public boolean isInRange(String ipAddress) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(ipAddress);
        BigInteger start = new BigInteger(1, this.networkAddress.getAddress());
        BigInteger end = new BigInteger(1, this.broadcastAddress.getAddress());
        BigInteger target = new BigInteger(1, address.getAddress());
        int st = start.compareTo(target);
        int te = target.compareTo(end);
        return (st < 0 || st == 0) && (te < 0 || te == 0);
    }
}

class NetworkCreator {
    static Network create(String cidr) {
        if (!cidr.contains("/")) {
            throw new IllegalArgumentException("Not an valid CIDR format: " + cidr);
        }
        try {
            return createOrThrow(cidr);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Could not create ip interval from cidr: " + cidr, e);
        }
    }

    private static Network createOrThrow(String cidr) throws UnknownHostException {
        int index = cidr.indexOf("/");
        String addressPart = cidr.substring(0, index);
        String networkPart = cidr.substring(index + 1);
        InetAddress inetAddress = InetAddress.getByName(addressPart);
        int prefixLength = Integer.parseInt(networkPart);

        ByteBuffer maskBuffer = getMaskBuffer(inetAddress);
        int targetSize = ipSize(inetAddress);
        BigInteger mask = (new BigInteger(1, maskBuffer.array())).not().shiftRight(prefixLength);
        ByteBuffer buffer = ByteBuffer.wrap(inetAddress.getAddress());
        BigInteger ipVal = new BigInteger(1, buffer.array());
        BigInteger startIp = ipVal.and(mask);
        BigInteger endIp = startIp.add(mask.not());

        byte[] startIpArr = toBytes(startIp.toByteArray(), targetSize);
        byte[] endIpArr = toBytes(endIp.toByteArray(), targetSize);

        InetAddress startAddress = InetAddress.getByAddress(startIpArr);
        InetAddress endAddress = InetAddress.getByAddress(endIpArr);
        return new Network(cidr, inetAddress, prefixLength, startAddress, endAddress);
    }

    private static int ipSize(InetAddress inetAddress) {
        return inetAddress.getAddress().length == 4
                ? 4
                : 16;
    }

    private static ByteBuffer getMaskBuffer(InetAddress inetAddress) {
        if (inetAddress.getAddress().length == 4) {
            return ByteBuffer
                    .allocate(4)
                    .putInt(-1);
        } else {
            return ByteBuffer.allocate(16)
                    .putLong(-1L)
                    .putLong(-1L);
        }
    }

    private static byte[] toBytes(byte[] array, int targetSize) {
        int counter = 0;
        List<Byte> newArr = new ArrayList<Byte>();
        while (counter < targetSize && (array.length - 1 - counter >= 0)) {
            newArr.add(0, array[array.length - 1 - counter]);
            counter++;
        }
        int size = newArr.size();
        for (int i = 0; i < (targetSize - size); i++) {
            newArr.add(0, (byte) 0);
        }
        byte[] ret = new byte[newArr.size()];
        for (int i = 0; i < newArr.size(); i++) {
            ret[i] = newArr.get(i);
        }
        return ret;
    }
}