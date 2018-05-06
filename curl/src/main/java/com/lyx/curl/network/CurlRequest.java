package com.lyx.curl.network;

public class CurlRequest {

    static {
        try {
            System.loadLibrary("curl");
            System.loadLibrary("curls");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送GET请求
     *
     * @param id  请求id
     * @param url 请求URL
     * @param pem pen证书绝对路径
     * @param key key密匙绝对路径
     * @param crt crt证书绝对路径
     */
    public static native void getHttps(int id, String url, String pem, String key, String crt);

    /**
     * 发送POST请求
     *
     * @param id   请求id
     * @param url  请求URL
     * @param body 请求体
     * @param pem  pen证书绝对路径
     * @param key  key密匙绝对路径
     * @param crt  crt证书绝对路径
     */
    public static native void postHttps(int id, String url, String body, String pem, String key, String crt);

}