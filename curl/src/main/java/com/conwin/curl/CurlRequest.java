package com.conwin.curl;

public class CurlRequest {

    static {
        try {
            System.loadLibrary("curl");
            System.loadLibrary("native-lib");
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public static native String GetHttps(String url, String crtPath);

    public static native String PostHttps(String url, String body, String crtPath);

}