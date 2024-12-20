package com.odde.adsmockserver.adsapi;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface TwinCATADS extends Library {

    TwinCATADS INSTANCE = Native.load("TcAdsDll", TwinCATADS.class);

    // ADS API functions
    long AdsPortOpen();
    long AdsPortClose();
    long AdsGetLocalAddress(AmsAddr pAddr);
    long AdsSyncReadWriteReq(AmsAddr pAddr, int indexGroup, int indexOffset, int cbReadLength, Pointer pReadData, int cbWriteLength, Pointer pWriteData);
    long AdsSyncReadReq(AmsAddr pAddr, int indexGroup, int indexOffset, int cbLength, Pointer pData);
    long AdsSyncWriteReq(AmsAddr pAddr, int indexGroup, int indexOffset, int cbLength, Pointer pData);
    long AdsSyncReadDeviceInfoReq(AmsAddr pAddr, Pointer pDeviceName, AdsVersion pVersion);

    // Ads defines
    int ADSIGRP_SYM_HNDBYNAME = 0xF003;
    int ADSIGRP_SYM_VALBYHND = 0xF005;
    int ADSIGRP_SYM_RELEASEHND = 0xF006;

}
