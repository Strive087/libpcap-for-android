#include "com_example_zdl_NDKTools.h"
#include <stdio.h>
#include <pcap.h>
#include <android/log.h>
#define LOG   "libpcap"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG,__VA_ARGS__)


JNIEXPORT void JNICALL Java_com_example_zdl_NDKTools_runPcap(JNIEnv *env, jclass type){

    char *dev,errbuf[PCAP_ERRBUF_SIZE];
    dev = pcap_lookupdev(errbuf);
    LOGD("%s",errbuf);

 }
