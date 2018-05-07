package com.lyx.curl.crash;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.Gson;
import com.lyx.curl.network.HttpsRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * ExceptionService
 * <p>
 * author:  luoyingxing
 * date: 2018/4/24.
 */
public class ExceptionService extends IntentService {
    private String TAG = "ExceptionService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        String message = CrashUtils.getPref(getApplicationContext(), CrashUtils.CRASH_LOG);
        String time = CrashUtils.getPref(getApplicationContext(), CrashUtils.CRASH_TIME);

        if (null != message) {
            Crash crash = new Crash();
            crash.setException(message);
            crash.setTime(time);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            crash.setWidth("" + metrics.widthPixels);
            crash.setHeight("" + metrics.heightPixels);

            String[] appInfo = getPackage();
            if (null != appInfo) {
                crash.setPackageX(appInfo[0]);
                crash.setVerName(appInfo[1]);
                crash.setVerCode(appInfo[2]);
            }

            crash.setRoot(isRoot());

            //0 IMEI号 1 IESI号 2 手机型号 3 手机品牌  4 手机号
            String[] phoneInfo = getPhoneInfo();
            if (null != phoneInfo) {
                crash.setPhoneIMEI(phoneInfo[0]);
                crash.setPhoneModel(phoneInfo[2]);
                crash.setPhoneBrand(phoneInfo[3]);
            }

            String[] cpuInfo = getCPUInfo();
            crash.setCpuModel(cpuInfo[0]);

            crash.setSystemVer(Build.MODEL);
            crash.setCpuInstruction(Build.CPU_ABI);
            crash.setCpuInstruction2(Build.CPU_ABI2);

            //TODO: commit crash log to server
            String json = new Gson().toJson(crash);
            new HttpsRequest<Object>(CrashHandler.getInstance().getCrashUrl()) {

                @Override
                public void onStart() {
                    super.onStart();
                    Log.w(TAG, "start upload crash report");
                }

                @Override
                public void onSuccess(Object result) {
                    super.onSuccess(result);
                    Log.w(TAG, "upload crash report succeed");
                    CrashUtils.savePref(getApplicationContext(), CrashUtils.CRASH_LOG, null);
                }

                @Override
                public void onFailure(int status, String data) {
                    super.onFailure(status, data);
                    Log.w(TAG, "upload crash report failed:" + status);
                }

            }.addBody(json).post();
        }
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart()");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    public ExceptionService() {
        super("ExceptionService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ExceptionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent()");
    }

    /**
     * 获取设备信息
     */
    private String getDeviceInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("主板：" + Build.BOARD + "\n");
        sb.append("系统启动程序版本号：" + Build.BOOTLOADER + "\n");
        sb.append("系统定制商：" + Build.BRAND + "\n");
        sb.append("cpu指令集：" + Build.CPU_ABI + "\n");
        sb.append("cpu指令集2 " + Build.CPU_ABI2 + "\n");
        sb.append("设置参数： " + Build.DEVICE + "\n");
        sb.append("显示屏参数：" + Build.DISPLAY + "\n");
        sb.append("www.2cto.com无线电固件版本：" + Build.getRadioVersion() + "\n");
        sb.append("硬件识别码：" + Build.FINGERPRINT + "\n");
        sb.append("硬件名称：" + Build.HARDWARE + "\n");
        sb.append("HOST: " + Build.HOST + "\n");
        sb.append("修订版本列表：" + Build.ID + "\n");
        sb.append("硬件制造商：" + Build.MANUFACTURER + "\n");
        sb.append("版本：" + Build.MODEL + "\n");
        sb.append("硬件序列号：" + Build.SERIAL + "\n");
        sb.append("手机制造商： " + Build.PRODUCT + "\n");
        sb.append("描述Build的标签：" + Build.TAGS + "\n");
        sb.append("TIME:" + Build.TIME + "\n");
        sb.append("builder类型： " + Build.TYPE + "\n");
        sb.append("USER:" + Build.USER + "\n");
        return sb.toString();
    }

    /**
     * 获取软件包名,版本名，版本号
     *
     * @return 0 包名  1 版本名称  2 版本号
     */
    private String[] getPackage() {
        String[] info = new String[3];
        try {
            String packageName = getPackageName();
            String versionName = getPackageManager().getPackageInfo(packageName, 0).versionName;
            int versionCode = getPackageManager().getPackageInfo(packageName, 0).versionCode;

            info[0] = packageName;
            info[1] = versionName;
            info[2] = versionCode + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 获取手机是否root信息
     *
     * @return true is root, otherwise is false.
     */
    private boolean isRoot() {
        try {
            return !((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取android当前可用内存大小
     */
    private String getAvailMemory() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return "当前可用内存：" + Formatter.formatFileSize(getApplicationContext(), blockSize * availableBlocks);
    }

    /**
     * 获得系统总内存
     */
    private String getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "总内存大小：" + Formatter.formatFileSize(getBaseContext(), initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }

    /**
     * 获取IMEI号，IESI号，手机型号
     *
     * @return 0 IMEI号 1 IESI号 2 手机型号 3 手机品牌  4 手机号
     */
    private String[] getPhoneInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            TelephonyManager manager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            if (null != manager) {
                String[] info = new String[5];

                String imei = manager.getDeviceId();
                String imsi = manager.getSubscriberId();
                String type = Build.MODEL; // 手机型号
                String brand = Build.BRAND;//手机品牌
                String number = manager.getLine1Number(); // 手机号码，有的可得，有的不可得

                info[0] = imei;
                info[1] = imsi;
                info[2] = type;
                info[3] = brand;
                info[4] = number;

                return info;
            }
        }
        return null;
    }

    /**
     * 获取手机MAC地址
     * 只有手机开启wifi才能获取到mac地址
     */
    private String getMacAddress() {
        String result = "";
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        result = wifiInfo.getMacAddress();
        return "手机macAdd:" + result;
    }

    /**
     * 手机CPU信息
     *
     * @return 0 CPU型号 1 CPU频率
     */
    private String[] getCPUInfo() {
        String str1 = "/proc/cpuinfo";
        String str2;
        String[] cpu = {"", ""}; //1-cpu型号 //2-cpu频率
        String[] array;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            array = str2.split("\\s+");
            for (int i = 2; i < array.length; i++) {
                cpu[0] = cpu[0] + array[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            array = str2.split("\\s+");
            cpu[1] += array[2];
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String[]{cpu[0], cpu[1]};
    }

}