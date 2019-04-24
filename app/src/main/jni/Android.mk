LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := pcap

LOCAL_SRC_FILES := jniLibs/armeabi-v7a/libpcap.so

include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_LDLIBS += -llog

LOCAL_MODULE    := ndkpcap-jni

LOCAL_SRC_FILES := ndkpcap.c

LOCAL_CFLAGS	:= -DSYS_ANDROID=1 -Dyylval=pcap_lval -DHAVE_CONFIG_H  -D_U_="__attribute__((unused))" -I$(LOCAL_PATH)/libpcap-arm/include

LOCAL_SHARED_LIBRARIES := pcap

include $(BUILD_SHARED_LIBRARY)

