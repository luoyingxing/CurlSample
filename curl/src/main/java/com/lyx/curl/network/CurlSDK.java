package com.lyx.curl.network;

/**
 * CurlSDK
 * <p>
 * created by luoyingxing on 2020/10/19.
 */
public class CurlSDK {

    static {
        try {
            System.loadLibrary("curl");  //原始curl依赖库
            System.loadLibrary("curls"); //二次开发的curl库
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 发球HTTPS请求
     *
     * @param id      请求ID
     * @param methods 请求方式（0-GET 1-POST）
     * @param url     请求的URL
     * @param body    请求体
     * @param pem     证书存储的路径
     * @param key     证书存储的路径
     * @param crt     证书存储的路径
     */
    public static native void requestHttps(int id, int methods, String url, String body, String pem, String key, String crt);

    /**
     * 请求的数据回调
     *
     * @param id     请求ID
     * @param code   响应码
     * @param result 响应体
     */
    protected static void onResponse(int id, int code, String result) {
        if (null != mResponseListener) {
            mResponseListener.onResponse(id, code, result);
        }
    }

    protected static ResponseListener mResponseListener;

    protected interface ResponseListener {
        void onResponse(int id, int code, String result);
    }

    protected static void setOnResponseListener(ResponseListener listener) {
        mResponseListener = listener;
    }
}