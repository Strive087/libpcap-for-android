package com.example.zdl;

public class NDKTools {

    static {
        System.loadLibrary("pcap");
        System.loadLibrary("ndkpcap-jni");
    }

    public static native void runPcap();
}
