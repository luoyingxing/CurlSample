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

    public static native String GetHttps(String url, String crtPath);

}