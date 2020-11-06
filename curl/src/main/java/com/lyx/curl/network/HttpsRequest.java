package com.lyx.curl.network;

import android.text.TextUtils;

import com.lyx.curl.runnable.LooperKit;
import com.google.gson.Gson;
import com.lyx.curl.runnable.ThreadPoolManager;

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
    /**
     * 超时时间，默认14秒
     */
    private int timeout = 14;
    /**
     * 存放请求头的map
     */
    private Map<String, String> mHeader = new HashMap<>();
    /**
     * 存放请求参数的map
     */
    private Map<String, Object> mParamKeyValues = new HashMap<>();

    public void onResponse(final int status, final String header, final String data) {
        LooperKit.runOnMainThreadAsync(new Runnable() {
            @Override
            public void run() {
                parseData(status, header, data);
            }
        });
    }

    public HttpsRequest(String url) {
        this.url = url;
        init();
    }

    public HttpsRequest(String url, int time) {
        this.url = url;
        this.timeout = time;
        init();
    }

    private static final String ContentType = "application/json; charset=utf-8";

    protected void init() {
        //默认加上ContentType请求头
        addHeader("Content-Type", ContentType);
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

    /**
     * 添加请求头
     *
     * @param key   key
     * @param value value
     * @return 当前请求对象
     */
    public HttpsRequest addHeader(String key, String value) {
        mHeader.put(key, value);
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param key   key
     * @param value value
     * @return 当前请求对象
     */
    public HttpsRequest addParam(String key, Object value) {
        mParamKeyValues.put(key, value);
        return this;
    }

    /**
     * GET
     *
     * @param params
     * @return
     */
    protected String paramsToString(Map<String, Object> params) {
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

    /**
     * POST
     *
     * @param params
     * @return
     */
    protected String paramsToJson(Map<String, Object> params) {
        if (null == params) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("{");

        for (String key : params.keySet()) {
            builder.append('\"');
            builder.append(key);
            builder.append('\"');
            builder.append(':');

            Object value = params.get(key);
            if (value instanceof String) {
                builder.append('\"');
                builder.append(value);
                builder.append('\"');
            } else {
                builder.append(value);
            }
            builder.append(",");
        }

        if (builder.length() > 1) {
            builder = builder.deleteCharAt(builder.length() - 1);
            builder.append("}");
            return builder.toString();
        }

        return null;
    }

    private void startThreadTask() {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                request();
            }
        });
    }

    private void request() {
        setId(Curl.getInstance().getFreeRequestId());
        Curl.getInstance().addRequest(this);

        String[] headers;
        if (null != mHeader && mHeader.size() > 0) {
            headers = new String[mHeader.size() * 2];
            int i = 0;
            for (String key : mHeader.keySet()) {
                headers[2 * i] = key;
                headers[2 * i + 1] = mHeader.get(key);
                i++;
            }
        } else {
            headers = new String[]{};
        }

        if (0 == requestMethod) {
            String params = paramsToString(mParamKeyValues);
            if (!TextUtils.isEmpty(params)) {
                url = url + "?" + params;
            }

            CurlSDK.requestHttps(id, requestMethod, timeout, url, headers, null, Curl.getInstance().getPemPath(), Curl.getInstance().getKeyPath(), Curl.getInstance().getCrtPath());
        } else {
            if (null == body) {
                body = paramsToJson(mParamKeyValues);
            }
            CurlSDK.requestHttps(id, requestMethod, timeout, url, headers, body, Curl.getInstance().getPemPath(), Curl.getInstance().getKeyPath(), Curl.getInstance().getCrtPath());
        }
    }

    private void parseData(int status, String header, String data) {
        onResult(status, header, data);

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

    public void onResult(int code, String header, String data) {
    }

    public void onSuccess(T result) {
    }

    public void onFailure(int code, String data) {
    }

    public void onFinish() {

    }

    private Type getType() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            //参数化类型
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            //返回表示此类型实际类型参数的 Type 对象的数组
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            return actualTypeArguments[0];
        }

        return genericSuperclass;
    }

}