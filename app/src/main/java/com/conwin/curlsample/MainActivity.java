package com.conwin.curlsample;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.conwin.curl.CurlRequest;
import com.conwin.curl.Test;


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
            String result = CurlRequest.GetHttps("https://api.jingyun.cn/opid2host?opid=test", mCertPath);

            Log.i("MainActivity", "域名登陆 响应结果：  " + result);

            String[] str = result.split(",", 2);

            return result;
        }
    }

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