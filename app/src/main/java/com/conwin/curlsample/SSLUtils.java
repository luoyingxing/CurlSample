package com.conwin.curlsample;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SSLUtils
 * <p>
 * Created by luoyingxing on 2017/5/5.
 */

public class SSLUtils {

    public static void initSSL(final Context context, final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                copyAssets(context, "ANDROID.key", "ANDROID.key", path);
                copyAssets(context, "ANDROID.crt", "ANDROID.crt", path);
                copyAssets(context, "jingyun.root.pem", "jingyun.root.pem", path);
            }
        }).start();
    }

    private static void copyAssets(Context context, String assetsName, String saveName, String path) {
        String filename = path + "/" + saveName;
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdir();
        try {
            if (!(new File(filename)).exists()) {
                InputStream is = context.getResources().getAssets().open(assetsName);
                FileOutputStream fos = new FileOutputStream(filename);
                byte[] buffer = new byte[7168];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String stringToMD5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}