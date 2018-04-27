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

jmethodID g_method_onGetHttps = NULL;

#define LOG_TAG "glog"
#define LOGW(a)  __android_log_write(ANDROID_LOG_WARN,LOG_TAG,a)
#define MAX_RECV_BUFFEN_LEN 1024

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

//void resHttps(int statusCode, char* data){
//    if(g_env != NULL && g_method_onGetHttps != NULL){
//
//        jstring result = (*g_env)->NewStringUTF(g_env, data);
//        (*g_env)->CallStaticVoidMethod(g_env, g_res_class,g_method_onGetHttps,statusCode,result);
//        (*g_env)->DeleteLocalRef (g_env, result);
//
//    }
//}

size_t write_data(void *ptr, size_t size, size_t nmemb, void *stream) {
    if (size * nmemb < MAX_RECV_BUFFEN_LEN) {
        memcpy((char *) stream, (char *) ptr, size * nmemb);
    }
    // resHttps(200,ptr);
    return size * nmemb;
}


void reqHttps(JNIEnv *env, jstring sUrl, jstring crtPath, char *pOutBuffer) {
    const char *pCaPath = (*env)->GetStringUTFChars(env, crtPath, 0);

    char pCaCrt[256] = {0};
    char pAndroidKey[256] = {0};
    char pAndroidCrt[256] = {0};

    strcat(pCaCrt, pCaPath);
    strcat(pAndroidKey, pCaPath);
    strcat(pAndroidCrt, pCaPath);

    strcat(pCaCrt, "/jingyun.root.pem");
    strcat(pAndroidKey, "/ANDROID.key");
    strcat(pAndroidCrt, "/ANDROID.crt");

    LOGW(pCaPath);
    LOGW(pCaCrt);
    LOGW(pAndroidKey);
    LOGW(pAndroidCrt);


    LOGW("reqHttps 1");
    const char *pUrl = (*env)->GetStringUTFChars(env, sUrl, 0);
    LOGW("reqHttps 2");
    LOGW("pUrl=");
    LOGW(pUrl);
    CURL * curl;

    char recvData[MAX_RECV_BUFFEN_LEN] = {0};


    curl_global_init(CURL_GLOBAL_ALL);
    curl = curl_easy_init();
    curl_easy_setopt(curl, CURLOPT_URL, pUrl);

    curl_easy_setopt(curl, CURLOPT_CAPATH, pCaPath);
    curl_easy_setopt(curl, CURLOPT_CAINFO, pCaCrt);

    curl_easy_setopt(curl, CURLOPT_SSLCERT, pAndroidCrt);
    curl_easy_setopt(curl, CURLOPT_SSLCERTTYPE, "PEM");

    curl_easy_setopt(curl, CURLOPT_SSLKEY, pAndroidKey);
    curl_easy_setopt(curl, CURLOPT_SSLKEYTYPE, "PEM");

    // curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, head_data);
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_data);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, &recvData);

    CURLcode code = curl_easy_perform(curl);
    LOGW("reqHttps 3.1");
    if (code != CURLE_OK) {

        char log[44] ={0};
        sprintf(log,"curl err code=%d",code);
        LOGW(log);

        curl_easy_cleanup(curl);
        strcpy(pOutBuffer, "-1");
        return;

    }
    LOGW("reqHttps 4");
    long statusCode = 0;
    code = curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &statusCode);
    if (code != CURLE_OK) {
        curl_easy_cleanup(curl);
        strcpy(pOutBuffer, "-2");
        return;
    }

    LOGW("reqHttps 5");

    if (statusCode != 200) {
        curl_easy_cleanup(curl);

        char str[25] = {0};
        sprintf(str, "%ld", statusCode);
        strcpy(pOutBuffer, str);
        return;
    }
    if (strlen(recvData) == 0) {
        curl_easy_cleanup(curl);
        strcpy(pOutBuffer, "-5");
        return;
    }

    strcpy(pOutBuffer, "200,");
    strcat(pOutBuffer, recvData);
    LOGW("reqHttps 6");

    curl_easy_cleanup(curl);
    return;
}

JNIEXPORT jstring JNICALL
Java_com_conwin_curl_CurlRequest_GetHttps(JNIEnv *env, jobject obj, jstring url, jstring crtPath) {

    char pOutBuffer[MAX_RECV_BUFFEN_LEN + 3] = {0};
    reqHttps(env, url, crtPath, pOutBuffer);
    jstring pResult = (*g_env)->NewStringUTF(g_env, pOutBuffer);
    return pResult;

}

//
//
//
//
///*
//* Register several native methods for one class.
//*/
//
//
static JNINativeMethod gMethods[] = {
        {"GetHttps", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) Java_com_conwin_curl_CurlRequest_GetHttps}

};

static int registerNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = (*env)->FindClass(env, className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

//
///*
//* Register native methods for all classes we know about.
//*/
//
static int registerNatives(JNIEnv *env) {
    if (!registerNativeMethods(env, JNI_REQ_CLASS, gMethods,
                               sizeof(gMethods) / sizeof(gMethods[0])))
        return JNI_FALSE;
    return JNI_TRUE;
}

///*
//* Set some test stuff up.
//*
//* Returns the JNI version on success, -1 on failure.
//*/
jint JNI_OnLoad(JavaVM *vm, void *reserved) {

    LOGW("JNI_OnLoad");
    g_jvm_j = vm;

    JNIEnv *env = NULL;
    jint result = -1;
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {

        return -1;
    }

    if (!registerNatives(env)) {//ע��

        return -1;
    }
    g_env = env;
    /* success -- return valid version number */
    result = JNI_VERSION_1_4;

    jclass tmp = (*env)->FindClass(env, JNI_RES_CLASS);
    g_res_class = (jclass) ((*env)->NewGlobalRef(env, tmp));

    if (g_res_class != NULL) {
        g_method_onGetHttps = (*env)->GetStaticMethodID(env, g_res_class, "onGetHttps", "(ILjava/lang/String;)V");
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
