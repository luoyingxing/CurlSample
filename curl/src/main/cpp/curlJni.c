//
// curl request
//
// 本函数主要实现GET和POST请求功能，以及响应回调函数。
//
// Created by luoyingxing on 2018/4/27.
// Update by luoyingxing on 2020/10/20.

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


#define JNI_CurlSDK_CLASS "com/lyx/curl/network/CurlSDK"
#define LOG_TAG "CurlLib"

jclass g_res_class = NULL;
jmethodID g_method_onResponse = NULL;

#define LOGD(a) __android_log_write(ANDROID_LOG_DEBUG, LOG_TAG, a)
#define LOGI(a) __android_log_write(ANDROID_LOG_INFO, LOG_TAG, a)
#define LOGE(a) __android_log_write(ANDROID_LOG_ERROR, LOG_TAG, a)
#define LOGW(a) __android_log_write(ANDROID_LOG_WARN, LOG_TAG, a)

struct ReceiveStruct {
    char *memory;
    size_t size;
};

/**
 * 请求响应函数
 *
 * @param env
 * @param id
 * @param code
 * @param data
 */
void onResponse(JNIEnv *env, jint id, jint code, char *headerData, char *data) {
    if (g_method_onResponse != NULL && g_res_class != NULL) {
        jstring header = (*env)->NewStringUTF(env, headerData);
        jstring result = (*env)->NewStringUTF(env, data);
        (*env)->CallStaticVoidMethod(env, g_res_class, g_method_onResponse, id, code, header,
                                     result);
        (*env)->DeleteLocalRef(env, header);
        (*env)->DeleteLocalRef(env, result);
    }
}

/**
 *
 * @param buffer
 * @param size
 * @param nmemb
 * @param stream
 * @return
 */
size_t write_data(void *contents, size_t size, size_t nmemb, void *userp) {
    size_t realSize = size * nmemb;
    struct ReceiveStruct *mem = (struct ReceiveStruct *) userp;

    //根据每次被调用获得的数据重新动态分配缓存区的大小
    char *ptr = realloc(mem->memory, mem->size + realSize + 1);
    if (ptr == NULL) {
        LOGE("Not Enough Memory (realloc returned NULL)");
        return 0;
    }

    mem->memory = ptr;
    memcpy(&(mem->memory[mem->size]), contents, realSize);
    mem->size += realSize;
    mem->memory[mem->size] = 0;

    return realSize;
}

void requestHttps(JNIEnv *env, jint id, jint methods, jint timeout, jstring sUrl,
                  jobjectArray headerArray, jstring body, jstring pem, jstring key, jstring crt) {
    CURL *curl = NULL;
    curl = curl_easy_init();

    if (curl == NULL) {
        onResponse(env, id, 600, NULL, NULL);
        return;
    }

    //设置请求的地址
    const char *pUrl = (*env)->GetStringUTFChars(env, sUrl, 0);
    curl_easy_setopt(curl, CURLOPT_URL, pUrl);

    //设置请求头
    struct curl_slist *headers = NULL;
    if (headerArray != NULL) {
        int headerLen = (*env)->GetArrayLength(env, headerArray);
        for (int i = 0; i < headerLen; i++) {
            if (i % 2 == 0) { //key:value 取双数的为key
                jobject keyObj = (*env)->GetObjectArrayElement(env, headerArray, i);
                const char *header_key = (*env)->GetStringUTFChars(env, (jstring) keyObj, 0);

                jobject valueObj = (*env)->GetObjectArrayElement(env, headerArray, i + 1);
                const char *header_value = (*env)->GetStringUTFChars(env, (jstring) valueObj, 0);

                char header[255] = {0};
                sprintf(header, "%s:%s", header_key, header_value);
                headers = curl_slist_append(headers, header);

                (*env)->ReleaseStringUTFChars(env, keyObj, header_key);
                (*env)->ReleaseStringUTFChars(env, valueObj, header_value);
                (*env)->DeleteLocalRef(env, keyObj);
                (*env)->DeleteLocalRef(env, valueObj);
            }
        }

        if (headers != NULL) {
            curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);
        }
    }

    //设置问非0表示本次操作为post，methods参数0为GET  1为POST
    curl_easy_setopt(curl, CURLOPT_POST, methods);
    if (methods != 0) {
        const char *request_body = (*env)->GetStringUTFChars(env, body, 0);
        if (request_body != NULL) {
            curl_easy_setopt(curl, CURLOPT_POSTFIELDS, request_body);   //post参数

            (*env)->ReleaseStringUTFChars(env, body, request_body);
        }
    }

//  char reqBody[255] = {0};
//  sprintf(reqBody, "request body: %s", request_body);
//  LOGD(reqBody);

    curl_easy_setopt(curl, CURLOPT_VERBOSE, 1);                 //打印调试信息
    curl_easy_setopt(curl, CURLOPT_TIMEOUT, timeout);           //请求超时时间(单位S)

    //设置SSL证书
    const char *pemPath = (*env)->GetStringUTFChars(env, pem, 0);
    const char *keyPath = (*env)->GetStringUTFChars(env, key, 0);
    const char *crtsPath = (*env)->GetStringUTFChars(env, crt, 0);
    char pCaCrt[256] = {0};
    char pAndroidKey[256] = {0};
    char pAndroidCrt[256] = {0};
    strcat(pCaCrt, pemPath);
    strcat(pAndroidKey, keyPath);
    strcat(pAndroidCrt, crtsPath);
    curl_easy_setopt(curl, CURLOPT_CAINFO, pCaCrt);
    curl_easy_setopt(curl, CURLOPT_SSLCERT, pAndroidCrt);
    curl_easy_setopt(curl, CURLOPT_SSLCERTTYPE, "PEM");
    curl_easy_setopt(curl, CURLOPT_SSLKEY, pAndroidKey);
    curl_easy_setopt(curl, CURLOPT_SSLKEYTYPE, "PEM");

    struct ReceiveStruct responseData;
    responseData.memory = malloc(1);
    responseData.size = 0;

    struct ReceiveStruct responseHeader;
    responseHeader.memory = malloc(1);
    responseHeader.size = 0;

    //设置数据回调函数
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_data);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void *) &responseData);

    //设置响应头函数
    curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, write_data);
    curl_easy_setopt(curl, CURLOPT_HEADERDATA, (void *) &responseHeader);

    CURLcode code = curl_easy_perform(curl);  //code为处理结果，不是响应码

    if (code == CURLE_OK) {
        long status = 0;
        curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &status);
        curl_slist_free_all(headers);
        curl_easy_cleanup(curl);

        onResponse(env, id, status, responseHeader.memory, responseData.memory);
    } else {
        curl_slist_free_all(headers);
        curl_easy_cleanup(curl);

        onResponse(env, id, code, NULL, NULL);
    }


    (*env)->ReleaseStringUTFChars(env, sUrl, pUrl);
    (*env)->ReleaseStringUTFChars(env, pem, pemPath);
    (*env)->ReleaseStringUTFChars(env, key, keyPath);
    (*env)->ReleaseStringUTFChars(env, crt, crtsPath);
    free(responseHeader.memory);
    free(responseData.memory);
}

JNIEXPORT void JNICALL
Java_com_lyx_curl_network_CurlSDK_requestHttps(JNIEnv *env, jobject obj, jint id, jint methods,
                                               jint timeout, jstring url, jobjectArray headers,
                                               jstring body, jstring pen, jstring key,
                                               jstring crt) {
    requestHttps(env, id, methods, timeout, url, headers, body, pen, key, crt);
}

static JNINativeMethod methods[] = {
        {"requestHttps", "(IIILjava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", (void *) Java_com_lyx_curl_network_CurlSDK_requestHttps}
};

/**
 * 注册本地方法
 * @param env
 * @param className
 * @param method
 * @param numMethods
 * @return
 */
static int
registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *method, int numMethods) {
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
    if (!registerNativeMethods(env, JNI_CurlSDK_CLASS, methods,
                               sizeof(methods) / sizeof(methods[0]))) {
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
    LOGW("curlLib on load");

    JNIEnv *env = NULL;
    jint result;
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }

    if (!registerNatives(env)) {
        return -1;
    }

    result = JNI_VERSION_1_4;

    //加载响应类方法
    jclass tmp = (*env)->FindClass(env, JNI_CurlSDK_CLASS);
    g_res_class = (jclass) ((*env)->NewGlobalRef(env, tmp));

    if (g_res_class != NULL) {
        g_method_onResponse = (*env)->GetStaticMethodID(env, g_res_class, "onResponse",
                                                        "(IILjava/lang/String;Ljava/lang/String;)V");
    }

    LOGW("curl global init");
    curl_global_init(CURL_GLOBAL_ALL);

    return result;
}


#ifdef __cplusplus
}
#endif
