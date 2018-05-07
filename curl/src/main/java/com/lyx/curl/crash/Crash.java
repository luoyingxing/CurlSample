package com.lyx.curl.crash;

import com.google.gson.annotations.SerializedName;

/**
 * author:  luoyingxing
 * date: 2018/4/24.
 */
public class Crash {
    /**
     * width : 1080(屏幕分辨率)
     * height : 1920(屏幕分辨率)
     * verName : 1.0.1(APP版本名称)
     * verCode : 100(APP版本号)
     * package : con.conwin.smartalarm(APP包名)
     * phoneModel : FRD-DL00(手机型号)
     * phoneBrand : honor(手机品牌)
     * systemVer : 6.0.1(系统版本名称)
     * cpuModel : aarch64(CPU型号)
     * cpuInstruction : armeabi-v7a（cpu指令集）
     * cpuInstruction2 : armeabi-v7a（cpu指令集2）
     * phoneIMEI : 8751615615656（手机IMEI号）
     * root : false（手机是否root）
     * time : 2018-4-24 12:15:01(奔溃时间)
     * exception : Java NullException...(奔溃日志)
     */
    private String width;
    private String height;
    private String verName;
    private String verCode;
    @SerializedName("package")
    private String packageX;
    private String phoneModel;
    private String phoneBrand;
    private String systemVer;
    private String cpuModel;
    private String cpuInstruction;
    private String cpuInstruction2;
    private String phoneIMEI;
    private boolean root;
    private String time;
    private String exception;

    public Crash() {
    }

    public Crash(String width, String height, String verName, String verCode, String packageX, String phoneModel, String phoneBrand, String systemVer, String cpuModel, String cpuInstruction, String cpuInstruction2, String phoneIMEI, boolean root, String time, String exception) {
        this.width = width;
        this.height = height;
        this.verName = verName;
        this.verCode = verCode;
        this.packageX = packageX;
        this.phoneModel = phoneModel;
        this.phoneBrand = phoneBrand;
        this.systemVer = systemVer;
        this.cpuModel = cpuModel;
        this.cpuInstruction = cpuInstruction;
        this.cpuInstruction2 = cpuInstruction2;
        this.phoneIMEI = phoneIMEI;
        this.root = root;
        this.time = time;
        this.exception = exception;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getVerName() {
        return verName;
    }

    public void setVerName(String verName) {
        this.verName = verName;
    }

    public String getVerCode() {
        return verCode;
    }

    public void setVerCode(String verCode) {
        this.verCode = verCode;
    }

    public String getPackageX() {
        return packageX;
    }

    public void setPackageX(String packageX) {
        this.packageX = packageX;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getPhoneBrand() {
        return phoneBrand;
    }

    public void setPhoneBrand(String phoneBrand) {
        this.phoneBrand = phoneBrand;
    }

    public String getSystemVer() {
        return systemVer;
    }

    public void setSystemVer(String systemVer) {
        this.systemVer = systemVer;
    }

    public String getCpuModel() {
        return cpuModel;
    }

    public void setCpuModel(String cpuModel) {
        this.cpuModel = cpuModel;
    }

    public String getCpuInstruction() {
        return cpuInstruction;
    }

    public void setCpuInstruction(String cpuInstruction) {
        this.cpuInstruction = cpuInstruction;
    }

    public String getCpuInstruction2() {
        return cpuInstruction2;
    }

    public void setCpuInstruction2(String cpuInstruction2) {
        this.cpuInstruction2 = cpuInstruction2;
    }

    public String getPhoneIMEI() {
        return phoneIMEI;
    }

    public void setPhoneIMEI(String phoneIMEI) {
        this.phoneIMEI = phoneIMEI;
    }

    public boolean getRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "Crash{" +
                "width='" + width + '\'' +
                ", height='" + height + '\'' +
                ", verName='" + verName + '\'' +
                ", verCode='" + verCode + '\'' +
                ", packageX='" + packageX + '\'' +
                ", phoneModel='" + phoneModel + '\'' +
                ", phoneBrand='" + phoneBrand + '\'' +
                ", systemVer='" + systemVer + '\'' +
                ", cpuModel='" + cpuModel + '\'' +
                ", cpuInstruction='" + cpuInstruction + '\'' +
                ", cpuInstruction2='" + cpuInstruction2 + '\'' +
                ", phoneIMEI='" + phoneIMEI + '\'' +
                ", root='" + root + '\'' +
                ", time='" + time + '\'' +
                ", exception='" + exception + '\'' +
                '}';
    }
}
