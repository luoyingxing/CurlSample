package com.lyx.curl.usage;

import com.google.gson.annotations.SerializedName;

/**
 * Info 移动设备的硬件参数
 * <p>
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
 * <p>
 * createed by luoyingxing on 2020-10-21
 */
public class Info {
    private String width;              //屏幕分辨率
    private String height;             //屏幕分辨率
    private String phoneModel;         //手机型号
    private String phoneBrand;         //手机品牌
    private String systemVer;          //系统版本名称
    private String cpuModel;           //CPU型号
    private String cpuInstruction;     //cpu指令集
    private String cpuInstruction2;    //cpu指令集2
    private String phoneIMEI;          //手机IMEI号
    private boolean root;              //手机是否root

    private String verName;            //APP版本名称
    private String verCode;            //APP版本号
    @SerializedName("package")
    private String packageX;           //APP包名
    private String time;               //生成信息时间

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

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}