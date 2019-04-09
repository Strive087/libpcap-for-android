LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE:=pcap

LOCAL_SRC_FILES:=jniLibs/armeabi-v7a/libpcap.so

include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_LDLIBS += -llog

LOCAL_MODULE    := ndkpcap-jni

LOCAL_SRC_FILES := ndkpcap.c

LOCAL_C_INCLUDES += /usr/local/libpcap-arm/include

LOCAL_SHARED_LIBRARIES := pcap

include $(BUILD_SHARED_LIBRARY)

