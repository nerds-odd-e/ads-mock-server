package com.odde.adsmockserver.adsapi;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AdsVersion extends Structure {
    public byte version;
    public byte revision;
    public short build;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("version", "revision", "build");
    }
}
