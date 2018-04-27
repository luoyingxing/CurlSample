package com.conwin.curl;

import android.util.Log;

/**
 * CurlResponse
 * <p>
 * Created by Gimo on 2017/3/14.
 */

public class CurlResponse {

    static void onGetHttps(int status, String data) {
        String log = "https res status code :" + status;
        if (status == 200) {
            log = log + ",data :" + data;
        }
        Log.i("CurlResponse", log);
    }

}