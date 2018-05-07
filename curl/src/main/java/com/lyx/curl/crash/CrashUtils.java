package com.lyx.curl.crash;

import android.content.Context;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * CrashUtils
 * <p>
 * author:  luoyingxing
 * date: 2018/5/7.
 */
public class CrashUtils {
    public static final String CRASH_LOG = "crash_log";
    public static final String CRASH_TIME = "crash_time";

    public static void savePref(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static String getPref(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, null);
    }

    public static String currentDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(new Date(System.currentTimeMillis()));
    }
}