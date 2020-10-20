package com.conwin.curlsample;

import android.app.Application;

import com.lyx.curl.crash.CrashHandler;
import com.lyx.curl.network.Curl;

/**
 * author:  luoyingxing
 * date: 2018/5/2.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //CrashHandler.getInstance().init(this, null, "https://cos.conwin.cn:8443/log/crash");

        Curl.getInstance().initialize(getFilesDir().getAbsolutePath() + "/cert/jingyun.root.pem",
                getFilesDir().getAbsolutePath() + "/cert/ANDROID.key",
                getFilesDir().getAbsolutePath() + "/cert/ANDROID.crt");
    }
}