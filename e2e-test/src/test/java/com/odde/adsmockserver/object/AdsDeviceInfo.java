package com.odde.adsmockserver.object;

import com.odde.adsmockserver.adsapi.AdsVersion;

public class AdsDeviceInfo {

    public final String name;
    public byte version;
    public byte revision;
    public short build;

    public AdsDeviceInfo(AdsVersion adsVersion, String name) {
        this.name = name;
        this.version = adsVersion.version;
        this.revision = adsVersion.revision;
        this.build = adsVersion.build;
    }
}
