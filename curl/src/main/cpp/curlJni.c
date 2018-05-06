//
// curl request
//
// 本函数主要实现GET和POST请求功能，以及响应回调函数。
//
// Created by luoyingxing on 2018/4/27.
//

#ifdef __cplusplus
extern "C"{
#endif

#include <jni.h>
#include <stdio.h>
#include <android/log.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include "curl/curl.h"
#include "curl/easy.h"

#define JNI_REQ_CLASS "com/lyx/curl/network/CurlRequest"

#define JNI_RES_CLASS "com/lyx/curl/network/CurlResponse"

#define LOG_TAG "lib-curl"

jclass g_res_class = NULL;
jmethodID g_method_onResponse = NULL;

#define LOGD(a) __android_log_write(ANDROID_LOG_DEBUG, LOG_TAG, a)
#define LOGI(a) __android_log_write(ANDROID_LOG_INFO, LOG_TAG, a)
#define LOGE(a) __android_log_write(ANDROID_LOG_ERROR, LOG_TAG, a)
#define LOGW(a) __android_log_write(ANDROID_LOG_WARN, LOG_TAG, a)

#define MAX_BUFFER_LEN 1024

/**
 * GET、POST的响应函数
 *
 * @param env
 * @param id
 * @param status
 * @param data
 */
void onResponse(JNIEnv *env, jint id, jint status, jstring data) {
    if (g_method_onResponse != NULL && g_res_class != NULL) {
        jstring result = (*env)->NewStringUTF(env, data);
        (*env)->CallStaticVoidMethod(env, g_res_class, g_method_onResponse, id, status, result);
    }
}

/**
 * GET请求，CURL的回调函数
 * @param ptr
 * @param size
 * @param nmemb
 * @param stream
 * @return
 */
size_t write_data(void *ptr, size_t size, size_t nmemb, void *stream) {
    if (size * nmemb < MAX_BUFFER_LEN) {
        memcpy((char *) stream, (char *) ptr, size * nmemb);
    }
    return size * nmemb;
}

/**
 *
 * @param env
 * @param id
 * @param sUrl
 * @param pem
 * @param key
 * @param crt
 * @param buffer
 */
void
reqHttps(JNIEnv *env, jint id, jstring sUrl, jstring pem, jstring key, jstring crt) {
    const char *pemPath = (*env)->GetStringUTFChars(env, pem, 0);
    const char *keyPath = (*env)->GetStringUTFChars(env, key, 0);
    const char *crtsPath = (*env)->GetStringUTFChars(env, crt, 0);

    char pCaCrt[256] = {0};
    char pAndroidKey[256] = {0};
    char pAndroidCrt[256] = {0};

    strcat(pCaCrt, pemPath);
    strcat(pAndroidKey, keyPath);
    strcat(pAndroidCrt, crtsPath);

    const char *pUrl = (*env)->GetStringUTFChars(env, sUrl, 0);
    char url[255] = {0};
    sprintf(url, "Access: %s", pUrl);
    LOGW(url);

    CURL *curl;

    char receive_data[MAX_BUFFER_LEN] = {0};

    curl = curl_easy_init();
    curl_easy_setopt(curl, CURLOPT_URL, pUrl);

    curl_easy_setopt(curl, CURLOPT_CAINFO, pCaCrt);

    curl_easy_setopt(curl, CURLOPT_SSLCERT, pAndroidCrt);
    curl_easy_setopt(curl, CURLOPT_SSLCERTTYPE, "PEM");

    curl_easy_setopt(curl, CURLOPT_SSLKEY, pAndroidKey);
    curl_easy_setopt(curl, CURLOPT_SSLKEYTYPE, "PEM");

    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_data);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, &receive_data);

    CURLcode code = curl_easy_perform(curl);

    //请求结果
    if (code != CURLE_OK) {
        curl_easy_cleanup(curl);
        //TODO: onResponse failed
        onResponse(env, id, code, NULL);
        return;
    }

    long status = 0;
    curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &status);

    //TODO: onResponse succeed
    onResponse(env, id, status, receive_data);

    curl_easy_cleanup(curl);
    return;
}

/**
 *
 * @param env
 * @param obj
 * @param id
 * @param url
 * @param pem
 * @param key
 * @param crt
 */
JNIEXPORT void JNICALL
Java_com_lyx_curl_network_CurlRequest_getHttps(JNIEnv *env, jobject obj, jint id, jstring url,
                                          jstring pem, jstring key, jstring crt) {
    reqHttps(env, id, url, pem, key, crt);
}

/**
 *
 * @param buffer
 * @param size
 * @param nmemb
 * @param stream
 * @return
 */
size_t response_data(void *buffer, size_t size, size_t nmemb, void *stream) {
    if (size * nmemb < MAX_BUFFER_LEN) {
        memcpy((char *) stream, (char *) buffer, size * nmemb);
    }
    return size * nmemb;
}

/**
 *
 * @param env
 * @param id
 * @param sUrl
 * @param body
 * @param pem
 * @param key
 * @param crt
 * @param buffer
 */
void postHttps(JNIEnv *env, jint id, jstring sUrl, jstring body, jstring pem,
               jstring key, jstring crt) {
    const char *request_body = (*env)->GetStringUTFChars(env, body, 0);

    const char *pemPath = (*env)->GetStringUTFChars(env, pem, 0);
    const char *keyPath = (*env)->GetStringUTFChars(env, key, 0);
    const char *crtsPath = (*env)->GetStringUTFChars(env, crt, 0);

    char pCaCrt[256] = {0};
    char pAndroidKey[256] = {0};
    char pAndroidCrt[256] = {0};

    strcat(pCaCrt, pemPath);
    strcat(pAndroidKey, keyPath);
    strcat(pAndroidCrt, crtsPath);

    const char *pUrl = (*env)->GetStringUTFChars(env, sUrl, 0);
    char url[255] = {0};
    sprintf(url, "Access: %s", pUrl);
    LOGW(url);

    CURL *curl;

    char receive_data[MAX_BUFFER_LEN] = {0};

    curl_global_init(CURL_GLOBAL_ALL);
    curl = curl_easy_init();
    curl_easy_setopt(curl, CURLOPT_URL, pUrl);

    struct curl_slist *headers = NULL;

    headers = curl_slist_append(headers, "Accept:application/json");
    headers = curl_slist_append(headers, "Content-Type:application/json");
    headers = curl_slist_append(headers, "charset:utf-8");

    curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);

    curl_easy_setopt(curl, CURLOPT_POSTFIELDS, request_body);   //post参数
    curl_easy_setopt(curl, CURLOPT_POST, 1);                    //设置问非0表示本次操作为post
    curl_easy_setopt(curl, CURLOPT_VERBOSE, 1);                 //打印调试信息
    curl_easy_setopt(curl, CURLOPT_TIMEOUT, 20);                // 超时(单位S)

    curl_easy_setopt(curl, CURLOPT_CAINFO, pCaCrt);

    curl_easy_setopt(curl, CURLOPT_SSLCERT, pAndroidCrt);
    curl_easy_setopt(curl, CURLOPT_SSLCERTTYPE, "PEM");

    curl_easy_setopt(curl, CURLOPT_SSLKEY, pAndroidKey);
    curl_easy_setopt(curl, CURLOPT_SSLKEYTYPE, "PEM");

    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, response_data);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, &receive_data);


    CURLcode code = curl_easy_perform(curl);

    if (code != CURLE_OK) {
        curl_easy_cleanup(curl);
        //TODO: onResponse failed
        onResponse(env, id, code, NULL);
        return;
    }

    long status = 0;
    curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &status);
    //TODO: onResponse succeed
    onResponse(env, id, status, receive_data);

    curl_easy_cleanup(curl);
    return;
}

/**
 *
 * @param env
 * @param obj
 * @param id
 * @param url
 * @param body
 * @param pem
 * @param key
 * @param crt
 */
JNIEXPORT void JNICALL
Java_com_lyx_curl_network_CurlRequest_postHttps(JNIEnv *env, jobject obj, jint id, jstring url,
                                           jstring body, jstring pem, jstring key,
                                           jstring crt) {
    postHttps(env, id, url, body, pem, key, crt);
}

/**
 * GET、POST请求的方法定义
 */
static JNINativeMethod methods[] = {
        {"getHttps",  "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",                   (void *) Java_com_lyx_curl_network_CurlRequest_getHttps},
        {"postHttps", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", (void *) Java_com_lyx_curl_network_CurlRequest_postHttps}
};

/**
 * 注册本地方法
 * @param env
 * @param className
 * @param method
 * @param numMethods
 * @return
 */
static int registerNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *method, int numMethods) {
    jclass clazz;
    clazz = (*env)->FindClass(env, className);

    if (clazz == NULL) {
        return JNI_FALSE;
    }

    if ((*env)->RegisterNatives(env, clazz, method, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

/**
 * 注册本地方法
 * @param env
 * @return
 */
static int registerNatives(JNIEnv *env) {
    if (!registerNativeMethods(env, JNI_REQ_CLASS, methods,
                               sizeof(methods) / sizeof(methods[0]))) {
        return JNI_FALSE;
    }

    if (!registerNativeMethods(env, JNI_REQ_CLASS, methods,
                               sizeof(methods) / sizeof(methods[1]))) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

/**
 * 第一次加载JNI
 * @param vm
 * @param reserved
 * @return
 */
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGW("jni loading");

    JNIEnv *env = NULL;
    jint result;
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }

    if (!registerNatives(env)) {
        return -1;
    }

    result = JNI_VERSION_1_4;

    //TODO: 加载响应类方法
    jclass tmp = (*env)->FindClass(env, JNI_RES_CLASS);
    g_res_class = (jclass) ((*env)->NewGlobalRef(env, tmp));

    if (g_res_class != NULL) {
        g_method_onResponse = (*env)->GetStaticMethodID(env, g_res_class, "onResponse",
                                                        "(IILjava/lang/String;)V");
    }

    LOGW("curl global init");
    curl_global_init(CURL_GLOBAL_ALL);

    return result;
}


#ifdef __cplusplus
}
#endif
