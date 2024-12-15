package com.odde.adsmockserver;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;
import io.cucumber.java.en.Then;

public class AdsSteps {

    @Then("ads operation should:")
    public void adsOperationShould(String expression) {
        long port = TwinCATADS.INSTANCE.AdsPortOpen();
        System.out.println("ADS Port Opened with handle: " + port);
        TwinCATADS.AmsAddr addr = new TwinCATADS.AmsAddr();
        long err = TwinCATADS.INSTANCE.AdsGetLocalAddress(addr);
        System.out.println("err = " + err);
        System.out.println("addr.netId = " + addr.netId.str());
        addr.port = 851;

        IntByReference nHdlVar = new IntByReference();
        int nHdlVarSize = Integer.SIZE / Byte.SIZE;
        String symbolName = "PC_PLC.b_error";
        Memory symbolNameMem = new Memory(symbolName.length() + 1); // +1 for null termination
        symbolNameMem.setString(0, symbolName);
        err = TwinCATADS.INSTANCE.AdsSyncReadWriteReq(addr, TwinCATADS.ADSIGRP_SYM_HNDBYNAME, 0, nHdlVarSize, nHdlVar.getPointer(), symbolName.length(), symbolNameMem);
        System.out.println("err = " + err);
        System.out.println("nHdlVar.getValue() = " + nHdlVar.getValue());

        ShortByReference nData = new ShortByReference();
        int nDataSize = Short.SIZE / Byte.SIZE;
        err = TwinCATADS.INSTANCE.AdsSyncReadReq(addr, TwinCATADS.ADSIGRP_SYM_VALBYHND, nHdlVar.getValue(), nDataSize, nData.getPointer());
        System.out.println("err = " + err);
        System.out.println("nData.getValue() = " + nData.getValue());

        err = TwinCATADS.INSTANCE.AdsSyncWriteReq(addr, TwinCATADS.ADSIGRP_SYM_RELEASEHND, 0, nHdlVarSize, nHdlVar.getPointer());
        System.out.println("err = " + err);

        err = TwinCATADS.INSTANCE.AdsPortClose();
        System.out.println("err = " + err);

    }
}
