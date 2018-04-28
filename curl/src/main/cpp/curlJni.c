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
#include <unistd.h>
#include "curl/curl.h"

#define JNI_REQ_CLASS "com/conwin/curl/CurlRequest"

JNIEnv *g_env;

#define LOG_TAG "curl"

#define LOGD(a) __android_log_write(ANDROID_LOG_DEBUG, LOG_TAG, a)
#define LOGI(a) __android_log_write(ANDROID_LOG_INFO, LOG_TAG, a)
#define LOGE(a) __android_log_write(ANDROID_LOG_ERROR, LOG_TAG, a)
#define LOGW(a) __android_log_write(ANDROID_LOG_WARN, LOG_TAG, a)

#define MAX_BUFFER_LEN 1024

size_t write_data(void *ptr, size_t size, size_t nmemb, void *stream) {
    if (size * nmemb < MAX_BUFFER_LEN) {
        memcpy((char *) stream, (char *) ptr, size * nmemb);
    }
    return size * nmemb;
}

/**
 *
 * @param env
 * @param sUrl
 * @param crtPath
 * @param buffer
 */
void reqHttps(JNIEnv *env, jstring sUrl, jstring crtPath, char *buffer) {
    const char *caPath = (*env)->GetStringUTFChars(env, crtPath, 0);

    char pCaCrt[256] = {0};
    char pAndroidKey[256] = {0};
    char pAndroidCrt[256] = {0};

    strcat(pCaCrt, caPath);
    strcat(pAndroidKey, caPath);
    strcat(pAndroidCrt, caPath);

    strcat(pCaCrt, "/jingyun.root.pem");
    strcat(pAndroidKey, "/ANDROID.key");
    strcat(pAndroidCrt, "/ANDROID.crt");

    LOGD(caPath);

    const char *pUrl = (*env)->GetStringUTFChars(env, sUrl, 0);
    char url[255] = {0};
    sprintf(url, "Access Url : %s", pUrl);
    LOGI(url);

    CURL *curl;

    char receive_data[MAX_BUFFER_LEN] = {0};

    curl_global_init(CURL_GLOBAL_ALL);
    curl = curl_easy_init();
    curl_easy_setopt(curl, CURLOPT_URL, pUrl);

    curl_easy_setopt(curl, CURLOPT_CAPATH, caPath);
    curl_easy_setopt(curl, CURLOPT_CAINFO, pCaCrt);

    curl_easy_setopt(curl, CURLOPT_SSLCERT, pAndroidCrt);
    curl_easy_setopt(curl, CURLOPT_SSLCERTTYPE, "PEM");

    curl_easy_setopt(curl, CURLOPT_SSLKEY, pAndroidKey);
    curl_easy_setopt(curl, CURLOPT_SSLKEYTYPE, "PEM");

    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_data);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, &receive_data);

    CURLcode code = curl_easy_perform(curl);

    char log[100] = {0};
    sprintf(log, "curl easy perform result: %d", code);
    LOGD(log);

    if (code != CURLE_OK) {
        curl_easy_cleanup(curl);
        char res[10] = {0};
        sprintf(res, "%d", code);
        strcpy(buffer, res);
        return;
    }

    long statusCode = 0;
    code = curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &statusCode);
    if (code != CURLE_OK) {
        curl_easy_cleanup(curl);
        char res[10] = {0};
        sprintf(res, "%d", code);
        strcpy(buffer, res);
        return;
    }

    if (statusCode != 200) {
        curl_easy_cleanup(curl);
        char str[25] = {0};
        sprintf(str, "%ld", statusCode);
        strcpy(buffer, str);
        return;
    }

    if (strlen(receive_data) == 0) {
        curl_easy_cleanup(curl);
        char res[10] = {0};
        sprintf(res, "%d", code);
        strcpy(buffer, res);
        return;
    }

    strcpy(buffer, "200,");
    strcat(buffer, receive_data);

    curl_easy_cleanup(curl);
    return;
}

/**
 *
 * @param env
 * @param obj
 * @param url 需要访问的地址
 * @param crtPath 证书路径（APP外存目录）
 * @return string 返回结果
 */
JNIEXPORT jstring JNICALL
Java_com_conwin_curl_CurlRequest_getHttps(JNIEnv *env, jobject obj, jstring url, jstring crtPath) {
    char buffer[MAX_BUFFER_LEN + 3] = {0};
    reqHttps(env, url, crtPath, buffer);
    jstring result = (*g_env)->NewStringUTF(g_env, buffer);
    return result;
}


size_t response_data(void *buffer, size_t size, size_t nmemb, void *stream) {
    if (size * nmemb < MAX_BUFFER_LEN) {
        memcpy((char *) stream, (char *) buffer, size * nmemb);
    }

    return size * nmemb;
}

/**
 *
 * @param env
 * @param sUrl
 * @param crtPath
 * @param body
 * @param buffer
 */
void postHttps(JNIEnv *env, jstring sUrl, jstring crtPath, jstring body, char *buffer) {
    const char *caPath = (*env)->GetStringUTFChars(env, crtPath, 0);
    const char *request_body = (*env)->GetStringUTFChars(env, body, 0);

    char pCaCrt[256] = {0};
    char pAndroidKey[256] = {0};
    char pAndroidCrt[256] = {0};

    strcat(pCaCrt, caPath);
    strcat(pAndroidKey, caPath);
    strcat(pAndroidCrt, caPath);

    LOGD(caPath);

    strcat(pCaCrt, "/jingyun.root.pem");
    strcat(pAndroidKey, "/ANDROID.key");
    strcat(pAndroidCrt, "/ANDROID.crt");

    const char *pUrl = (*env)->GetStringUTFChars(env, sUrl, 0);
    char url[255] = {0};
    sprintf(url, "Access Url : %s", pUrl);
    LOGI(url);

    CURL *curl;

    char receive_data[MAX_BUFFER_LEN] = {0};

    curl_global_init(CURL_GLOBAL_ALL);
    curl = curl_easy_init();
    curl_easy_setopt(curl, CURLOPT_URL, pUrl);

    struct curl_slist *headers = NULL;

    //增加HTTP header
    headers = curl_slist_append(headers, "Accept:application/json");
    headers = curl_slist_append(headers, "Content-Type:application/json");
    headers = curl_slist_append(headers, "charset:utf-8");

    curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);

    curl_easy_setopt(curl, CURLOPT_POSTFIELDS, request_body); //post参数
    curl_easy_setopt(curl, CURLOPT_POST, 1); //设置问非0表示本次操作为post
    curl_easy_setopt(curl, CURLOPT_VERBOSE, 1); //打印调试信息
    curl_easy_setopt(curl, CURLOPT_HEADER, 1); //将响应头信息和相应体一起传给write_data
    curl_easy_setopt(curl, CURLOPT_TIMEOUT, 20);           // 超时(单位S)

//    curl_easy_setopt(curl, CURLFORM_CONTENTTYPE, "application/json");

    curl_easy_setopt(curl, CURLOPT_CAPATH, caPath);
    curl_easy_setopt(curl, CURLOPT_CAINFO, pCaCrt);

    curl_easy_setopt(curl, CURLOPT_SSLCERT, pAndroidCrt);
    curl_easy_setopt(curl, CURLOPT_SSLCERTTYPE, "PEM");

    curl_easy_setopt(curl, CURLOPT_SSLKEY, pAndroidKey);
    curl_easy_setopt(curl, CURLOPT_SSLKEYTYPE, "PEM");

    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, response_data);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, &receive_data);


    CURLcode code = curl_easy_perform(curl);

    char log[100] = {0};
    sprintf(log, "curl easy perform result: %d", code);
    LOGD(log);

    if (code != CURLE_OK) {
        curl_easy_cleanup(curl);
        char res[10] = {0};
        sprintf(res, "%d", code);
        strcpy(buffer, res);
        return;
    }

    long statusCode = 0;
    code = curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &statusCode);
    if (code != CURLE_OK) {
        curl_easy_cleanup(curl);
        char res[10] = {0};
        sprintf(res, "%d", code);
        strcpy(buffer, res);
        return;
    }

    if (statusCode != 200) {
        curl_easy_cleanup(curl);
        char str[25] = {0};
        sprintf(str, "%ld", statusCode);
        strcpy(buffer, str);
        return;
    }

    if (strlen(receive_data) == 0) {
        curl_easy_cleanup(curl);
        char res[10] = {0};
        sprintf(res, "%d", code);
        strcpy(buffer, res);
        return;
    }

    strcpy(buffer, "200,");
    strcat(buffer, receive_data);

    curl_easy_cleanup(curl);
    return;
}

JNIEXPORT jstring JNICALL
Java_com_conwin_curl_CurlRequest_postHttps(JNIEnv *env, jobject obj, jstring url, jstring body,
                                           jstring crtPath) {
    char buffer[MAX_BUFFER_LEN + 3] = {0};
    postHttps(env, url, crtPath, body, buffer);
    jstring result = (*g_env)->NewStringUTF(g_env, buffer);
    return result;
}

static JNINativeMethod gMethods[] = {
        {"getHttps",  "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",                   (void *) Java_com_conwin_curl_CurlRequest_getHttps},
        {"postHttps", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) Java_com_conwin_curl_CurlRequest_postHttps}
};

static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *gMethods,
                                 int numMethods) {
    jclass clazz;
    clazz = (*env)->FindClass(env, className);

    if (clazz == NULL) {
        return JNI_FALSE;
    }

    LOGD(className);

    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

//Register native methods for all classes we know about.
static int registerNatives(JNIEnv *env) {
    if (!registerNativeMethods(env, JNI_REQ_CLASS, gMethods,
                               sizeof(gMethods) / sizeof(gMethods[0]))) {
        return JNI_FALSE;
    }

    if (!registerNativeMethods(env, JNI_REQ_CLASS, gMethods,
                               sizeof(gMethods) / sizeof(gMethods[1]))) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

// Set some test stuff up.
//
// Returns the JNI version on success, -1 on failure.
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGD("JNI_OnLoad");

    JNIEnv *env = NULL;
    jint result = -1;
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }

    if (!registerNatives(env)) {
        return -1;
    }

    g_env = env;

    result = JNI_VERSION_1_4;

    return result;
}


#ifdef __cplusplus
}
#endif
