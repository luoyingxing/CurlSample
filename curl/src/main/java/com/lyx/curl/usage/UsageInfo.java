package com.lyx.curl.usage;

/**
 * UsageInfo 用户使用情况的信息·<数据统计>
 * <p>
 * createed by luoyingxing on 2020-10-21
 */
public class UsageInfo extends Info {
    /**
     * 提交数据类型（启动App-1，停止App-2，具体页面-3xxx，）
     */
    private int type;
    /**
     * 提交平台（安卓-1、苹果-2）
     */
    private int platform;
    /**
     * 提交数据的时候，手机的定位位置（xx省xx市xx区）
     */
    private String address;
    /**
     * 网络类型（4G，WIFI）
     */
    private String network;

    public UsageInfo() {
    }

    public UsageInfo(int type, int platform) {
        this.type = type;
        this.platform = platform;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
}