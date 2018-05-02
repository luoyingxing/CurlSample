package com.conwin.curl;

import android.util.Log;

/**
 * CurlResponse
 * <p>
 * Created by luoyingxing on 2018/5/3.
 */

public class CurlResponse {
    public static onResponseListener mOnResponseListener;

    static void onResponse(int id, int status, String data) {
        Log.i("CurlResponse", "id:" + id + " status:" + status + " data:" + data);

        if (null != mOnResponseListener) {
            mOnResponseListener.onResponse(id, status, data);
        }
    }

    public interface onResponseListener {
        void onResponse(int id, int status, String data);
    }

    public static onResponseListener getResponseListener() {
        return mOnResponseListener;
    }

    public static void setOnResponseListener(onResponseListener listener) {
        CurlResponse.mOnResponseListener = listener;
    }
}