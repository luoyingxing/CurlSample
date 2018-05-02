package com.conwin.curl;

import android.text.TextUtils;

import com.conwin.curl.runnable.LooperKit;
import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpsRequest
 * <p>
 * Created by luoyingxing on 2018/5/2.
 */
public class HttpsRequest<T> {
    /**
     * 请求ID
     */
    private int id;

    protected int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    /**
     * 请求URL
     */
    private String url;
    /**
     * 请求体
     */
    private String body;
    /**
     * 0 GET
     * 1 POST
     */
    private int requestMethod;

    public void onResponse(final int status, final String data) {
        LooperKit.runOnMainThreadAsync(new Runnable() {
            @Override
            public void run() {
                parseData(status, data);
            }
        });
    }

    public HttpsRequest() {
    }

    public HttpsRequest(String url) {
        this.url = url;
    }

    public void get() {
        requestMethod = 0;
        send();
    }

    public void post() {
        requestMethod = 1;
        send();
    }

    public HttpsRequest addBody(String body) {
        this.body = body;
        return this;
    }

    private void send() {
        onStart();
        startThreadTask();
    }

    private Map<String, Object> mParamKeyValues;

    public HttpsRequest addParam(String key, String value) {
        if (null == mParamKeyValues) {
            mParamKeyValues = new HashMap<>();
        }

        mParamKeyValues.put(key, value);
        return this;
    }

    private String paramsToString(Map<String, Object> params) {
        if (null == params) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        for (String key : params.keySet()) {
            builder.append(key);
            builder.append('=');
            builder.append(params.get(key));
            builder.append('&');
        }

        if (builder.length() > 0) {
            builder = builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    private void startThreadTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                request();
            }
        }).start();
    }

    private void request() {
        setId(Curl.getSingleton().getFreeRequestId());
        Curl.getSingleton().addRequest(this);

        if (0 == requestMethod) {
            String params = paramsToString(mParamKeyValues);
            if (!TextUtils.isEmpty(params)) {
                url = url + "?" + params;
            }

            CurlRequest.getHttps(id, url, Curl.getSingleton().getPemPath(), Curl.getSingleton().getKeyPath(), Curl.getSingleton().getCrtPath());
        } else {
            CurlRequest.postHttps(id, url, body, Curl.getSingleton().getPemPath(), Curl.getSingleton().getKeyPath(), Curl.getSingleton().getCrtPath());
        }
    }

    private void parseData(int status, String data) {
        onResult(status, data);

        if (200 == status) {
            T obj = null;
            if (!TextUtils.isEmpty(data)) {
                try {
                    obj = new Gson().fromJson(data, getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            onSuccess(obj);
        } else {
            onFailure(status, data);
        }

        onFinish();
    }


    public void onStart() {
    }

    public void onResult(int status, String data) {
    }

    public void onSuccess(T result) {
    }

    public void onFailure(int status, String data) {
    }

    public void onFinish() {

    }

    private Type getType() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType genericSuperclassType = (ParameterizedType) genericSuperclass;
        Type[] actualTypeArguments = genericSuperclassType.getActualTypeArguments();
        return actualTypeArguments[0];
    }

}