package com.odde.adsmockserver;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public interface TwinCATADS extends Library {

    TwinCATADS INSTANCE = Native.load("TcAdsDll", TwinCATADS.class);

    // ADS API functions
    long AdsPortOpen();

    long AdsPortClose();

    long AdsGetLocalAddress(AmsAddr pAddr);

    long AdsSyncReadWriteReq(AmsAddr pAddr, int indexGroup, int indexOffset, int cbReadLength, Pointer pReadData, int cbWriteLength, Pointer pWriteData);

    long AdsSyncReadReq(AmsAddr pAddr, int indexGroup, int indexOffset, int cbLength, Pointer pData);

    long AdsSyncWriteReq(AmsAddr pAddr, int indexGroup, int indexOffset, int cbLength, Pointer pData);
    // Add more functions as needed

    int ADSIGRP_SYM_HNDBYNAME = 0xF003;
    int ADSIGRP_SYM_VALBYHND = 0xF005;
    int ADSIGRP_SYM_RELEASEHND = 0xF006;

    // Mapping structures from C++ to JNA
    class AmsNetId extends Structure {
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

    class AmsAddr extends Structure {
        public AmsNetId netId;
        public short port;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("netId", "port");
        }
    }

}
