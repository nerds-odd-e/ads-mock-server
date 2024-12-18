package com.odde.adsmockserver;

import com.odde.adsmockserver.adsapi.AmsAddr;
import com.odde.adsmockserver.adsapi.TwinCATADS;
import com.sun.jna.Memory;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;
import org.springframework.stereotype.Component;

@Component
public class AdsOperations {

    private static final int ADS_PORT = 851;
    private static final int ADS_HDL_VAR_SIZE = Integer.SIZE / Byte.SIZE;
    private static final int ADS_INT_SIZE = Short.SIZE / Byte.SIZE;
    private static final int ADS_BOOL_SIZE = 1;

    public short readIntSymbolByName(String name) {
        openPort();
        AmsAddr addr = getAmsAddr();
        IntByReference nHdlVar = getHandlerByName(addr, name);
        short nData = readIntSymbolByHandler(addr, nHdlVar);
        releaseHandler(addr, nHdlVar);
        closePort();
        return nData;
    }

    public boolean readBoolSymbolByName(String name) {
        openPort();
        AmsAddr addr = getAmsAddr();
        IntByReference nHdlVar = getHandlerByName(addr, name);
        boolean nData = readBoolSymbolByHandler(addr, nHdlVar);
        releaseHandler(addr, nHdlVar);
        closePort();
        return nData;
    }

    private void openPort() {
        TwinCATADS.INSTANCE.AdsPortOpen();
    }

    private void closePort() {
        long err = TwinCATADS.INSTANCE.AdsPortClose();
        throwIfError("Closing port", err);
    }

    private void releaseHandler(AmsAddr addr, IntByReference nHdlVar) {
        long err = TwinCATADS.INSTANCE.AdsSyncWriteReq(addr, TwinCATADS.ADSIGRP_SYM_RELEASEHND, 0, ADS_HDL_VAR_SIZE, nHdlVar.getPointer());
        throwIfError("Releasing handler: " + nHdlVar.getValue(), err);
    }

    private short readIntSymbolByHandler(AmsAddr addr, IntByReference nHdlVar) {
        ShortByReference nData = new ShortByReference();
        long err = TwinCATADS.INSTANCE.AdsSyncReadReq(addr, TwinCATADS.ADSIGRP_SYM_VALBYHND, nHdlVar.getValue(), ADS_INT_SIZE, nData.getPointer());
        throwIfError("Reading int symbol by handler: " + nHdlVar.getValue(), err);
        return nData.getValue();
    }

    private boolean readBoolSymbolByHandler(AmsAddr addr, IntByReference nHdlVar) {
        ByteByReference nData = new ByteByReference();
        long err = TwinCATADS.INSTANCE.AdsSyncReadReq(addr, TwinCATADS.ADSIGRP_SYM_VALBYHND, nHdlVar.getValue(), ADS_BOOL_SIZE, nData.getPointer());
        throwIfError("Reading bool symbol by handler: " + nHdlVar.getValue(), err);
        return nData.getValue() != 0;
    }

    private AmsAddr getAmsAddr() {
        AmsAddr addr = new AmsAddr();
        long err = TwinCATADS.INSTANCE.AdsGetLocalAddress(addr);
        throwIfError("AdsGetLocalAddress", err);
        addr.port = ADS_PORT;
        return addr;
    }

    private IntByReference getHandlerByName(AmsAddr addr, String name) {
        IntByReference nHdlVar = new IntByReference();
        Memory symbolNameMem = new Memory(name.length() + 1); // +1 for null termination
        symbolNameMem.setString(0, name);
        long err = TwinCATADS.INSTANCE.AdsSyncReadWriteReq(addr, TwinCATADS.ADSIGRP_SYM_HNDBYNAME, 0, ADS_HDL_VAR_SIZE, nHdlVar.getPointer(), name.length(), symbolNameMem);
        throwIfError("Getting handler by name: " + name, err);
        return nHdlVar;
    }

    private void throwIfError(String operationName, long err) {
        if (err != 0) {
            throw new IllegalStateException(operationName + " failed with error code: " + err);
        }
    }
}