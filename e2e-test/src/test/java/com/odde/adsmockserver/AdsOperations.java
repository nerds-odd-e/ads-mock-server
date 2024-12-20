package com.odde.adsmockserver;

import com.odde.adsmockserver.adsapi.AmsAddr;
import com.odde.adsmockserver.adsapi.TwinCATADS;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.*;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class AdsOperations {

    private static final int ADS_PORT = 851;
    private static final int ADS_HDL_VAR_SIZE = Integer.SIZE / Byte.SIZE;
    private static final int ADS_INT_SIZE = Short.SIZE / Byte.SIZE;
    private static final int ADS_DINT_SIZE = Integer.SIZE / Byte.SIZE;
    private static final int ADS_DOUBLE_SIZE = Double.SIZE / Byte.SIZE;
    private static final int ADS_FLOAT_SIZE = Float.SIZE / Byte.SIZE;
    private static final int ADS_BOOL_SIZE = 1;

    public double[] readLRealArraySymbolByName(String name, int size) {
        openPort();
        AmsAddr addr = getAmsAddr();
        IntByReference nHdlVar = getHandlerByName(addr, name);
        double[] nData = readLRealArraySymbolByHandler(addr, nHdlVar, size);
        releaseHandler(addr, nHdlVar);
        closePort();
        return nData;
    }

    private double[] readLRealArraySymbolByHandler(AmsAddr addr, IntByReference nHdlVar, int size) {
        try (Memory nData = new Memory(size * ADS_DOUBLE_SIZE)) {
            readSymbolByHandler(addr, nHdlVar.getValue(), ADS_DOUBLE_SIZE * size, nData, "Reading double array symbol by handler: ");
            return nData.getDoubleArray(0, size);
        }
    }

    public short readIntSymbolByName(String name) {
        return readSymbolByName(name, this::readIntSymbolByHandler);
    }

    public int readDIntSymbolByName(String name) {
        return readSymbolByName(name, this::readDIntSymbolByHandler);
    }

    public <T> T readSymbolByName(String name, BiFunction<AmsAddr, IntByReference, T> readByHandler) {
        openPort();
        AmsAddr addr = getAmsAddr();
        IntByReference nHdlVar = getHandlerByName(addr, name);
        T nData = readByHandler.apply(addr, nHdlVar);
        releaseHandler(addr, nHdlVar);
        closePort();
        return nData;
    }

    public boolean readBoolSymbolByName(String name) {
        return readSymbolByName(name, this::readBoolSymbolByHandler);
    }

    public double readLRealSymbolByName(String name) {
        return readSymbolByName(name, this::readLRealSymbolByHandler);
    }

    public float readRealSymbolByName(String name) {
        return readSymbolByName(name, this::readRealSymbolByHandler);
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
        readSymbolByHandler(addr, nHdlVar.getValue(), ADS_INT_SIZE, nData.getPointer(), "Reading int symbol by handler: ");
        return nData.getValue();
    }

    private int readDIntSymbolByHandler(AmsAddr addr, IntByReference nHdlVar) {
        IntByReference nData = new IntByReference();
        readSymbolByHandler(addr, nHdlVar.getValue(), ADS_DINT_SIZE, nData.getPointer(), "Reading dint symbol by handler: ");
        return nData.getValue();
    }

    private double readLRealSymbolByHandler(AmsAddr addr, IntByReference nHdlVar) {
        DoubleByReference nData = new DoubleByReference();
        readSymbolByHandler(addr, nHdlVar.getValue(), ADS_DOUBLE_SIZE, nData.getPointer(), "Reading double symbol by handler: ");
        return nData.getValue();
    }

    private float readRealSymbolByHandler(AmsAddr addr, IntByReference nHdlVar) {
        FloatByReference nData = new FloatByReference();
        readSymbolByHandler(addr, nHdlVar.getValue(), ADS_FLOAT_SIZE, nData.getPointer(), "Reading float symbol by handler: ");
        return nData.getValue();
    }

    private void readSymbolByHandler(AmsAddr addr, int value, int size, Pointer pointer, String errorMessagePrefix) {
        long err = TwinCATADS.INSTANCE.AdsSyncReadReq(addr, TwinCATADS.ADSIGRP_SYM_VALBYHND, value, size, pointer);
        throwIfError(errorMessagePrefix + value, err);
    }

    private boolean readBoolSymbolByHandler(AmsAddr addr, IntByReference nHdlVar) {
        ByteByReference nData = new ByteByReference();
        readSymbolByHandler(addr, nHdlVar.getValue(), ADS_BOOL_SIZE, nData.getPointer(), "Reading bool symbol by handler: ");
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