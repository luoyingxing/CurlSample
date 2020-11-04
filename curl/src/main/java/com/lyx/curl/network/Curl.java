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
public class Curl implements CurlSDK.ResponseListener {
    private volatile static Curl instance;
    private String pemPath;
    private String keyPath;
    private String crtPath;

    private List<HttpsRequest> httpsRequestList;

    private int requestId;

    protected int getFreeRequestId() {
        return ++requestId;
    }

    private Curl() {
        if (null == httpsRequestList) {
            httpsRequestList = new ArrayList<>();
        }
        CurlSDK.setOnResponseListener(this);
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

    protected String getPemPath() {
        return pemPath;
    }

    protected String getKeyPath() {
        return keyPath;
    }

    protected String getCrtPath() {
        return crtPath;
    }

    protected synchronized void addRequest(HttpsRequest request) {
        if (null == httpsRequestList) {
            httpsRequestList = new ArrayList<>();
        }
        httpsRequestList.add(request);
    }

    private synchronized void removeRequest(int id, int status, String header, String data) {
        if (null != httpsRequestList) {
            Iterator<HttpsRequest> iterator = httpsRequestList.iterator();
            while (iterator.hasNext()) {
                HttpsRequest request = iterator.next();
                if (null != request && request.getId() == id) {
                    request.onResponse(status, header, data);
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void onResponse(int id, int status, String header, String data) {
        if (null == httpsRequestList) {
            return;
        }

        removeRequest(id, status, header, data);
    }
}