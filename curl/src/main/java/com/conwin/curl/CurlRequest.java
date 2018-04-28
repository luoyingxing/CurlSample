package com.conwin.curl;

public class CurlRequest {

    static {
        try {
            System.loadLibrary("curl");
            System.loadLibrary("cwcurl");
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public static native String getHttps(String url, String crtPath);

    public static native String postHttps(String url, String body, String crtPath);

}