package com.odde.adsmockserver.adsapi;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AmsNetId extends Structure {
    public byte[] b = new byte[6];

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("b");
    }

    public String str() {
        StringBuilder decimalBuilder = new StringBuilder();
        for (byte value : b) {
            decimalBuilder.append((value & 0xFF) + " ");
        }
        return decimalBuilder.toString();
    }
}
