package com.conwin.curlsample;

import android.app.Application;

import com.conwin.curl.Curl;

/**
 * author:  luoyingxing
 * date: 2018/5/2.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Curl.getInstance().initialize(getFilesDir().getAbsolutePath() + "/cert/jingyun.root.pem",
                getFilesDir().getAbsolutePath() + "/cert/ANDROID.key",
                getFilesDir().getAbsolutePath() + "/cert/ANDROID.crt");
    }
}