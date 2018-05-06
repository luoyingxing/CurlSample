package com.lyx.curl.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Curl
 * <p>
 * author:  luoyingxing
 * date: 2018/5/2.
 */
public class Curl implements CurlResponse.ResponseListener {
    private volatile static Curl instance;
    private String pemPath;
    private String keyPath;
    private String crtPath;

    private List<HttpsRequest> httpsRequestList;

    private int requestId;

    public int getFreeRequestId() {
        return ++requestId;
    }

    private Curl() {
        if (null == httpsRequestList) {
            httpsRequestList = new ArrayList<>();
        }
        CurlResponse.setOnResponseListener(this);
    }

    public static Curl getInstance() {
        if (null == instance) {
            synchronized (Curl.class) {
                if (null == instance) {
                    instance = new Curl();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化相关证书的绝对路径
     *
     * @param pemPath .root.pem 证书路径
     * @param keyPath .key 密匙路径
     * @param crtPath .crt 证书路径
     */
    public void initialize(String pemPath, String keyPath, String crtPath) {
        this.pemPath = pemPath;
        this.keyPath = keyPath;
        this.crtPath = crtPath;
    }

    /**
     * 暂时不加载，等需要频繁使用CURL时再加载，避免内存资源浪费
     */
    private void loadLibrary() {
        try {
            System.loadLibrary("curl");
            System.loadLibrary("curls");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected String getPemPath() {
        return pemPath;
    }

    protected String getKeyPath() {
        return keyPath;
    }

    protected String getCrtPath() {
        return crtPath;
    }

    protected void addRequest(HttpsRequest request) {
        if (null == httpsRequestList) {
            httpsRequestList = new ArrayList<>();
        }
        httpsRequestList.add(request);
    }

    @Override
    public void onResponse(int id, int status, String data) {
        if (null == httpsRequestList) {
            return;
        }

        //多线程操作
        Iterator<HttpsRequest> iterator = httpsRequestList.iterator();
        while (iterator.hasNext()) {
            HttpsRequest request = iterator.next();
            if (request.getId() == id) {
                request.onResponse(status, data);
                iterator.remove();
            }
        }
    }
}