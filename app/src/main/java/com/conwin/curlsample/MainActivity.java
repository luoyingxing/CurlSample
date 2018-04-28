package com.conwin.curlsample;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.conwin.curl.CurlRequest;


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
                new LoginAsy().execute();
            }
        });

        mCertPath = getFilesDir().getAbsolutePath() + "/cert";
        SSLUtils.initSSL(this, mCertPath);
    }

    private class LoginAsy extends AsyncTask<String, Integer, String> {

        LoginAsy() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String domain) {
            super.onPostExecute(domain);
            Log.d("MainActivity", "onPostExecute");
            tv.setText("响应结果： " + domain);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
//            String result = CurlRequest.getHttps("https://api.jingyun.cn/opid2host?opid=test", mCertPath);
            String result = null;
            try {
                result = CurlRequest.postHttps("https://cos.conwin.cn:8443/log/crash", body, mCertPath);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.i("MainActivity", " 响应结果：  " + result);

            String[] str = result.split(",", 2);

            return result;
        }
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

    private class HttpsDomain {
        private String name;
        private String host;
        private int port;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

}