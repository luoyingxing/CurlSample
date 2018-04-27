package com.conwin.curl;

/**
 * author:  luoyingxing
 * date: 2018/4/25.
 */
public class Test {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native String stringFromJNI();

}
