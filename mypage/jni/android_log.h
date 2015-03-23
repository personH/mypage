#include <string.h>
#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <stdio.h>

#define  LOG_TAG    "txnetwork"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

/* Set to 1 to enable debug log traces. */
#define DEBUG 0
