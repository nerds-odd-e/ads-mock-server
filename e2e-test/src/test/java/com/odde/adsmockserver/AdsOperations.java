package com.odde.adsmockserver;

import com.odde.adsmockserver.adsapi.AdsVersion;
import com.odde.adsmockserver.adsapi.AmsAddr;
import com.odde.adsmockserver.adsapi.TwinCATADS;
import com.odde.adsmockserver.object.AdsDeviceInfo;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

@Component
public class AdsOperations {

    private static final int ADS_PORT = 851;
    private static final int ADS_HDL_VAR_SIZE = Integer.SIZE / Byte.SIZE;
    private static final int ADS_INT_SIZE = Short.SIZE / Byte.SIZE;
    private static final int ADS_DINT_SIZE = Integer.SIZE / Byte.SIZE;
    private static final int ADS_LREAL_SIZE = Double.SIZE / Byte.SIZE;
    private static final int ADS_REAL_SIZE = Float.SIZE / Byte.SIZE;
    private static final int ADS_BOOL_SIZE = 1;

    public double[] readLRealArraySymbolByName(String name, int size) {
        return readArraySymbolByName(name, size, this::readLRealArraySymbolByHandler);
    }

    public Boolean[] readBoolArraySymbolByName(String name, int size) {
        return readArraySymbolByName(name, size, this::readBoolArraySymbolByHandler);
    }

    private Boolean[] readBoolArraySymbolByHandler(AmsAddr addr, IntByReference nHdlVar, int size) {
        try (Memory nData = new Memory(size * ADS_BOOL_SIZE)) {
            readSymbolByHandler(addr, nHdlVar.getValue(), ADS_BOOL_SIZE * size, nData, "Reading bool array symbol by handler: ");
            byte[] byteArray = nData.getByteArray(0, size);
            return IntStream.range(0, byteArray.length)
                    .mapToObj(i -> byteArray[i] != 0)
                    .toArray(Boolean[]::new);
        }
    }

    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }

    private <T> T readArraySymbolByName(String name, int size, TriFunction<AmsAddr, IntByReference, Integer, T> readArraySymbolByHandler) {
        openPort();
        AmsAddr addr = getAmsAddr();
        IntByReference nHdlVar = getHandlerByName(addr, name);
        T nData = readArraySymbolByHandler.apply(addr, nHdlVar, size);
        releaseHandler(addr, nHdlVar);
        closePort();
        return nData;
    }

    public AdsDeviceInfo readDeviceInfo() {
        openPort();
        AmsAddr addr = getAmsAddr();
        AdsDeviceInfo adsDeviceInfo = readDeviceInfoByAddr(addr);
        closePort();
        return adsDeviceInfo;
    }

    public void writeBoolSymbolByName(String name, boolean value) {
        writeSymbolByName(name, value, this::writeBoolSymbolByHandler);
    }

    public void writeRealSymbolByName(String name, float value) {
        writeSymbolByName(name, value, this::writeRealSymbolByHandler);
    }

    public void writeDIntSymbolByName(String name, int value) {
        writeSymbolByName(name, value, this::writeDIntSymbolByHandler);
    }

    public void writeLRealArraySymbolByName(String name, Double[] value) {
        writeSymbolByName(name, value, this::writeLRealArraySymbolByHandler);
    }

    public float[] readRealArraySymbolByName(String name, int size) {
        return readArraySymbolByName(name, size, this::readRealArraySymbolByHandler);
    }

    private void writeLRealArraySymbolByHandler(AmsAddr amsAddr, IntByReference nHdlVar, Double[] value) {
        try (Memory memory = new Memory(ADS_LREAL_SIZE * value.length)) {
            for (int i = 0; i < value.length; i++) {
                memory.setDouble(i * ADS_LREAL_SIZE, value[i]);
            }
            writeSymbolByHandler(amsAddr, nHdlVar, memory, ADS_LREAL_SIZE * value.length, "Write lreal array symbol value: " + Arrays.toString(value));
        }
    }

    private void writeDIntSymbolByHandler(AmsAddr amsAddr, IntByReference nHdlVar, int value) {
        try (Memory memory = new Memory(ADS_DINT_SIZE)) {
            memory.setInt(0, value);
            writeSymbolByHandler(amsAddr, nHdlVar, memory, ADS_DINT_SIZE, "Write DInt symbol value: " + value);
        }
    }

    private void writeRealSymbolByHandler(AmsAddr amsAddr, IntByReference nHdlVar, float value) {
        try (Memory memory = new Memory(ADS_REAL_SIZE)) {
            memory.setFloat(0, value);
            writeSymbolByHandler(amsAddr, nHdlVar, memory, ADS_REAL_SIZE, "Write real symbol value: " + value);
        }
    }

    @FunctionalInterface
    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }

    private <T> void writeSymbolByName(String name, T value, TriConsumer<AmsAddr, IntByReference, T> consumer) {
        openPort();
        AmsAddr addr = getAmsAddr();
        IntByReference nHdlVar = getHandlerByName(addr, name);
        consumer.accept(addr, nHdlVar, value);
        releaseHandler(addr, nHdlVar);
        closePort();
    }

    public void writeLRealSymbolByName(String name, double value) {
        writeSymbolByName(name, value, this::writeLRealSymbolByHandler);
    }

    private void writeLRealSymbolByHandler(AmsAddr amsAddr, IntByReference nHdlVar, double value) {
        try (Memory memory = new Memory(ADS_LREAL_SIZE)) {
            memory.setDouble(0, value);
            writeSymbolByHandler(amsAddr, nHdlVar, memory, ADS_LREAL_SIZE, "Write lreal symbol value: " + value);
        }
    }

    private void writeBoolSymbolByHandler(AmsAddr addr, IntByReference nHdlVar, boolean value) {
        try (Memory memory = new Memory(ADS_BOOL_SIZE)) {
            memory.setByte(0, (byte) (value ? 1 : 0) );
            writeSymbolByHandler(addr, nHdlVar, memory, ADS_BOOL_SIZE, "Write bool symbol value: " + value);
        }
    }

    private AdsDeviceInfo readDeviceInfoByAddr(AmsAddr addr) {
        try (Memory deviceName = new Memory(16)) {
            AdsVersion adsVersion = new AdsVersion();
            long err = TwinCATADS.INSTANCE.AdsSyncReadDeviceInfoReq(addr, deviceName, adsVersion);
            throwIfError("Read device info", err);
            return new AdsDeviceInfo(adsVersion, deviceName.getString(0));
        }
    }

    private double[] readLRealArraySymbolByHandler(AmsAddr addr, IntByReference nHdlVar, int size) {
        try (Memory nData = new Memory(size * ADS_LREAL_SIZE)) {
            readSymbolByHandler(addr, nHdlVar.getValue(), ADS_LREAL_SIZE * size, nData, "Reading lreal array symbol by handler: ");
            return nData.getDoubleArray(0, size);
        }
    }

    private float[] readRealArraySymbolByHandler(AmsAddr addr, IntByReference nHdlVar, int size) {
        try (Memory nData = new Memory(size * ADS_REAL_SIZE)) {
            readSymbolByHandler(addr, nHdlVar.getValue(), ADS_REAL_SIZE * size, nData, "Reading real array symbol by handler: ");
            return nData.getFloatArray(0, size);
        }
    }

    public void writeIntSymbolByName(String name, short value) {
        writeSymbolByName(name, value, this::writeIntSymbolByHandler);
    }

    private void writeIntSymbolByHandler(AmsAddr addr, IntByReference nHdlVar, short value) {
        try (Memory memory = new Memory(ADS_INT_SIZE)) {
            memory.setShort(0, value);
            writeSymbolByHandler(addr, nHdlVar, memory, ADS_INT_SIZE, "Write int symbol value: " + value);
        }
    }

    private void writeSymbolByHandler(AmsAddr addr, IntByReference nHdlVar, Memory memory, int size, String errorMessage) {
        long err = TwinCATADS.INSTANCE.AdsSyncWriteReq(addr, TwinCATADS.ADSIGRP_SYM_VALBYHND, nHdlVar.getValue(), size, memory);
        throwIfError(errorMessage, err);
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
        readSymbolByHandler(addr, nHdlVar.getValue(), ADS_LREAL_SIZE, nData.getPointer(), "Reading lreal symbol by handler: ");
        return nData.getValue();
    }

    private float readRealSymbolByHandler(AmsAddr addr, IntByReference nHdlVar) {
        FloatByReference nData = new FloatByReference();
        readSymbolByHandler(addr, nHdlVar.getValue(), ADS_REAL_SIZE, nData.getPointer(), "Reading real symbol by handler: ");
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