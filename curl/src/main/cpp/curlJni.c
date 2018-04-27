//
// Created by Gimo on 2017/3/14.
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
#include "libs/curl.h"
/*发起本地调用的java类*/
#define JNI_REQ_CLASS "com/conwin/curl/CurlRequest"

/*接收反射回调的java类*/
#define JNI_RES_CLASS "com/conwin/curl/CurlResponse"

JavaVM *g_jvm_j;
JNIEnv *g_env;
jclass g_res_class = NULL;
jclass g_post_class = NULL;

jmethodID g_method_onGetHttps = NULL;
jmethodID g_method_PostGetHttps = NULL;

#define LOG_TAG "curl"

#define LOGW(a) __android_log_write(ANDROID_LOG_WARN, LOG_TAG, a)
#define LOGD(a) __android_log_write(ANDROID_LOG_DEBUG, LOG_TAG, a)
#define LOGI(a) __android_log_write(ANDROID_LOG_INFO, LOG_TAG, a)
#define LOGE(a) __android_log_write(ANDROID_LOG_ERROR, LOG_TAG, a)

#define MAX_BUFFER_LEN 1024

size_t head_data(void *ptr, size_t size, size_t nmemb, void *stream) {

    if (g_env != NULL && g_method_onGetHttps != NULL) {
        LOGW("jni head_data");
        jstring result = (*g_env)->NewStringUTF(g_env, (char *) ptr);
        (*g_env)->CallStaticVoidMethod(g_env, g_res_class, g_method_onGetHttps, 225, result);
        (*g_env)->DeleteLocalRef(g_env, result);

    } else {

    }
    return size * nmemb;
}

size_t write_data(void *ptr, size_t size, size_t nmemb, void *stream) {
    if (size * nmemb < MAX_BUFFER_LEN) {
        memcpy((char *) stream, (char *) ptr, size * nmemb);
    }
    // resHttps(200,ptr);
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
    LOGD(pCaCrt);
    LOGD(pAndroidKey);
    LOGD(pAndroidCrt);

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

    // curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, head_data);
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_data);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, &receive_data);

    CURLcode code = curl_easy_perform(curl);

    char log[100] = {0};
    sprintf(log, "curl easy perform result: %d", code);
    LOGW(log);

    LOGD("reqHttps 3");
    if (code != CURLE_OK) {
        curl_easy_cleanup(curl);
        strcpy(buffer, "-1");
        return;
    }

    LOGD("reqHttps 4");
    long statusCode = 0;
    code = curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &statusCode);
    if (code != CURLE_OK) {
        curl_easy_cleanup(curl);
        strcpy(buffer, "-2");
        return;
    }

    LOGD("reqHttps 5");
    if (statusCode != 200) {
        curl_easy_cleanup(curl);

        char str[25] = {0};
        sprintf(str, "%ld", statusCode);
        strcpy(buffer, str);
        return;
    }


    if (strlen(receive_data) == 0) {
        curl_easy_cleanup(curl);
        strcpy(buffer, "-5");
        return;
    }

    strcpy(buffer, "200,");
    strcat(buffer, receive_data);
    LOGD("reqHttps 6");

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
Java_com_conwin_curl_CurlRequest_GetHttps(JNIEnv *env, jobject obj, jstring url, jstring crtPath) {
    char buffer[MAX_BUFFER_LEN + 3] = {0};
    reqHttps(env, url, crtPath, buffer);
    jstring result = (*g_env)->NewStringUTF(g_env, buffer);
    return result;
}


size_t response_data(void *buffer, size_t size, size_t nmemb, void *stream) {
    LOGD("response_data");


    if (size * nmemb < MAX_BUFFER_LEN) {
        memcpy((char *) stream, (char *) buffer, size * nmemb);
    }

    return size * nmemb;
}

void postHttps(JNIEnv *env, jstring sUrl, jstring crtPath, jstring body, char *buffer) {
    const char *caPath = (*env)->GetStringUTFChars(env, crtPath, 0);
    const char *request_body = (*env)->GetStringUTFChars(env, body, 0);

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
    LOGD(pCaCrt);
    LOGD(pAndroidKey);
    LOGD(pAndroidCrt);

    const char *pUrl = (*env)->GetStringUTFChars(env, sUrl, 0);
    char url[255] = {0};
    sprintf(url, "Access Url : %s", pUrl);
    LOGI(url);

    CURL *curl;

    char receive_data[MAX_BUFFER_LEN] = {0};

    curl_global_init(CURL_GLOBAL_ALL);
    curl = curl_easy_init();
    curl_easy_setopt(curl, CURLOPT_URL, pUrl);

    curl_easy_setopt(curl, CURLOPT_POSTFIELDS, request_body); //post参数
    curl_easy_setopt(curl, CURLOPT_POST, 1); //设置问非0表示本次操作为post
    curl_easy_setopt(curl, CURLOPT_VERBOSE, 1); //打印调试信息
//    curl_easy_setopt(curl, CURLOPT_HEADER, 1); //将响应头信息和相应体一起传给write_data
//    curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1); //设置为非0,响应头信息location

    curl_easy_setopt(curl, CURLFORM_CONTENTTYPE, "application/json");

    curl_easy_setopt(curl, CURLOPT_CAPATH, caPath);
    curl_easy_setopt(curl, CURLOPT_CAINFO, pCaCrt);

    curl_easy_setopt(curl, CURLOPT_SSLCERT, pAndroidCrt);
    curl_easy_setopt(curl, CURLOPT_SSLCERTTYPE, "PEM");

    curl_easy_setopt(curl, CURLOPT_SSLKEY, pAndroidKey);
    curl_easy_setopt(curl, CURLOPT_SSLKEYTYPE, "PEM");

    // curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, head_data);
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, response_data);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, &receive_data);

    CURLcode code = curl_easy_perform(curl);

    char log[100] = {0};
    sprintf(log, "curl easy perform result: %d", code);
    LOGW(log);

    LOGD("reqHttps 3");
    if (code != CURLE_OK) {
        curl_easy_cleanup(curl);
        strcpy(buffer, "-1");
        return;
    }

    LOGD("reqHttps 4");
    long statusCode = 0;
    code = curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &statusCode);
    if (code != CURLE_OK) {
        curl_easy_cleanup(curl);
        strcpy(buffer, "-2");
        return;
    }

    LOGD("reqHttps 5");
    if (statusCode != 200) {
        curl_easy_cleanup(curl);

        char str[25] = {0};
        sprintf(str, "%ld", statusCode);
        strcpy(buffer, str);
        return;
    }


    if (strlen(receive_data) == 0) {
        curl_easy_cleanup(curl);
        strcpy(buffer, "-5");
        return;
    }

    strcpy(buffer, "200,");
    strcat(buffer, receive_data);
    LOGD("reqHttps 6");

    curl_easy_cleanup(curl);
    return;
}

JNIEXPORT jstring JNICALL
Java_com_conwin_curl_CurlRequest_PostHttps(JNIEnv *env, jobject obj, jstring url, jstring body,
                                           jstring crtPath) {
    char buffer[MAX_BUFFER_LEN + 3] = {0};
    postHttps(env, url, crtPath, body, buffer);
    jstring result = (*g_env)->NewStringUTF(g_env, buffer);
    return result;
}

static JNINativeMethod gMethods[] = {
        {"GetHttps",  "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",                   (void *) Java_com_conwin_curl_CurlRequest_GetHttps},
        {"PostHttps", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) Java_com_conwin_curl_CurlRequest_PostHttps}
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
    g_jvm_j = vm;

    JNIEnv *env = NULL;
    jint result = -1;
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }

    if (!registerNatives(env)) {
        return -1;
    }

    g_env = env;

    /* success -- return valid version number */
    result = JNI_VERSION_1_4;

    jclass tmp = (*env)->FindClass(env, JNI_RES_CLASS);
    g_res_class = (jclass) ((*env)->NewGlobalRef(env, tmp));

    jclass post_https = (*env)->FindClass(env, JNI_RES_CLASS);
    g_post_class = (jclass) ((*env)->NewGlobalRef(env, post_https));

    if (g_res_class != NULL) {
        g_method_onGetHttps = (*env)->GetStaticMethodID(env, g_res_class, "onGetHttps",
                                                        "(ILjava/lang/String;)V");
//        g_method_onVarPost = (*env)->GetStaticMethodID(env,g_res_class,"onVarsPost","(I[B)I");
//        g_method_onResponse = (*env)->GetStaticMethodID(env,g_res_class,"onResponse","(II[B[B)I");
//        g_method_connect = (*env)->GetStaticMethodID(env,g_res_class,"onConnect","(I[B)I");
//        g_method_outPutDebugString = (*env)->GetStaticMethodID(env,g_res_class,"outPutDebugString","(Ljava/lang/String;)V");
//        g_method_followed_update = (*env)->GetStaticMethodID(env,g_res_class,"followedUpdate","(ILjava/lang/String;)V");;
    }

    if (g_post_class != NULL) {
        g_method_PostGetHttps = (*env)->GetStaticMethodID(env, g_post_class, "onGetHttps",
                                                          "(ILjava/lang/String;)V");
//        g_method_onVarPost = (*env)->GetStaticMethodID(env,g_res_class,"onVarsPost","(I[B)I");
//        g_method_onResponse = (*env)->GetStaticMethodID(env,g_res_class,"onResponse","(II[B[B)I");
//        g_method_connect = (*env)->GetStaticMethodID(env,g_res_class,"onConnect","(I[B)I");
//        g_method_outPutDebugString = (*env)->GetStaticMethodID(env,g_res_class,"outPutDebugString","(Ljava/lang/String;)V");
//        g_method_followed_update = (*env)->GetStaticMethodID(env,g_res_class,"followedUpdate","(ILjava/lang/String;)V");;
    }


    return result;
}


#ifdef __cplusplus
}
#endif
