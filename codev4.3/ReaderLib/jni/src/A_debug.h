/*
 *           	  A file for Debug
 *                    2009.12.01
 *           modified:2009.12.01 by aleph        
 */

#ifndef A_DEBUG_H_
#define A_DEBUG_H_
//#define READER_JNI_DEBUG
//#define A_DEBUG	//no debug info
#define RMSDK_9_2    
#include <android/log.h>
/*
   ANDROID_LOG_UNKNOWN,
   ANDROID_LOG_DEFAULT,   
   ANDROID_LOG_VERBOSE,
   ANDROID_LOG_DEBUG,
   ANDROID_LOG_INFO,
   ANDROID_LOG_WARN,
   ANDROID_LOG_ERROR,
   ANDROID_LOG_FATAL,
   ANDROID_LOG_SILENT, 
   */
#ifndef LOG_TAG
#define LOG_TAG "pdfReader-c"
#endif
//#define LOG_ERROR(str) __android_log_write(ANDROID_LOG_ERROR,LOG_TAG, str) 
//#define LOG_DEBUG(str) __android_log_write(ANDROID_LOG_DEBUG,LOG_TAG, str) 
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOG_ERROR(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOG_DEBUG(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

#ifdef READER_JNI_DEBUG
#define FOOT_TRACE()  LOGI("Line: %8d \tFile: %s", __LINE__, __FILE__)
#define LOG_READER_DEBUG(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#else
#define FOOT_TRACE()  do {}while(0)
#define LOG_READER_DEBUG(...) do{}while(0)
#endif

#define __FUNCTION__ (__func__)

#ifdef A_DEBUG
#include <stdio.h>
//#define DEBUGINFO(a,b...) do {printf("%s", __FUNCTION__);printf( a, ##b);}while(0)
#define DEBUGINFO LOGI
#else
#define DEBUGINFO(a,b...) do{}while(0)
#endif

#ifdef A_DEBUG
#define DEBUG_NEW (std::nothrow)
#endif

struct resolution{
	int width ;
	int height ;
};
#endif  //A_DEBUG_H_

