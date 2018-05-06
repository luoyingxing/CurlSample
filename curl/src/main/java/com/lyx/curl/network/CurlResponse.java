package com.lyx.curl.network;

/**
 * CurlResponse
 * <p>
 * Created by luoyingxing on 2018/5/3.
 */

public class CurlResponse {
    protected static ResponseListener mResponseListener;

    protected static void onResponse(int id, int status, String data) {
        if (null != mResponseListener) {
            mResponseListener.onResponse(id, status, data);
        }
    }

    protected interface ResponseListener {
        void onResponse(int id, int status, String data);
    }

    protected static ResponseListener getResponseListener() {
        return mResponseListener;
    }

    protected static void setOnResponseListener(ResponseListener listener) {
        CurlResponse.mResponseListener = listener;
    }
}