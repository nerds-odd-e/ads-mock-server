package com.odde.adsmockserver.adsapi;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AmsAddr extends Structure {
    public AmsNetId netId;
    public short port;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("netId", "port");
    }
}
