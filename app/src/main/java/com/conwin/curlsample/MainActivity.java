package com.conwin.curlsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lyx.curl.network.HttpsRequest;

public class MainActivity extends AppCompatActivity {
    private String mCertPath;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.sample_text);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });

        mCertPath = getFilesDir().getAbsolutePath() + "/cert";
        SSLUtils.initSSL(this, mCertPath);
    }

    public void request() {
        new HttpsRequest<Object>("https://api.jingyun.cn/opid2host") {

            @Override
            public void onStart() {
                super.onStart();
                Log.i("MainActivity", "onStart()");
            }

            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
                Log.i("MainActivity", "onSuccess()");
            }

            @Override
            public void onResult(int status, String data) {
                super.onResult(status, data);
                Log.i("onResult", "onResult() status:" + status + "  data:" + data);
                tv.append("\nget\n");
                tv.append(data);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.i("MainActivity", "onFinish()");
            }

            @Override
            public void onFailure(int status, String data) {
                super.onFailure(status, data);
                Log.i("MainActivity", "onFailure()");
            }

        }.addParam("opid", "test")
                .get();

        new HttpsRequest<Object>("https://cos.conwin.cn:8443/log/crash") {

            @Override
            public void onStart() {
                super.onStart();
                Log.i("MainActivity----", "onStart()");
            }

            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
                Log.i("MainActivity----", "onSuccess()");
            }

            @Override
            public void onResult(int status, String data) {
                super.onResult(status, data);
                Log.i("onResult----", "onResult() status:" + status + "  data:" + data);
                tv.append("\npost\n");
                tv.append(data == null ? "data is null" : data);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.i("MainActivity----", "onFinish()");
            }

            @Override
            public void onFailure(int status, String data) {
                super.onFailure(status, data);
                Log.i("MainActivity----", "onFailure()");
            }

        }.addBody(body)
                .post();
    }

    private String body = "{\n" +
            "\"width\":\"1080\",\n" +
            "\"height\":\"1920\",\n" +
            "\"verName\":\"1.0.1\",\n" +
            "\"verCode\":\"1\",\n" +
            "\"package\":\"con.conwin.smartalarm\",\n" +
            "\"phoneModel\":\"FRD-DL00\",\n" +
            "\"phoneBrand\":\"honor\",\n" +
            "\"systemVer\":\"6.0.1\",\n" +
            "\"cpuModel\":\"aarch64\",\n" +
            "\"cpuInstruction\":\"armeabi-v7a\",\n" +
            "\"cpuInstruction2\":\"armeabi-v7a\",\n" +
            "\"phoneIMEI\":\"8751615615656\",\n" +
            "\"root\":\"false\",\n" +
            "\"time\":\"2018-4-24 12:15:01\",\n" +
            "\"exception\":\"Java NullException...\"\n" +
            "}";


}