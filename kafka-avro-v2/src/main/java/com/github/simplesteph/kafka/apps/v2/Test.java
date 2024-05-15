package com.github.simplesteph.kafka.apps.v2;

import static org.apache.commons.codec.binary.Hex.encodeHex;

import org.apache.commons.codec.binary.Base64;

public class Test {

    public static void main(String[] args) {
        final String encoded = "MDAtNjUwYmY0MmY1NWZjM2RlMzU5ODAzODg0NWRhOTY5MWQtNDQ5MGIzZWUyOTQxYWMyMC0wMQ==";
        final byte[] bytes = Base64.decodeBase64(encoded);
        final char[] chars = encodeHex(bytes);
        final String traceid = new String(chars);
        System.out.println(traceid);
    }
}
