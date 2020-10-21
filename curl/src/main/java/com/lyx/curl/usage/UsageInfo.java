package com.lyx.curl.usage;

/**
 * UsageInfo 用户使用情况的信息·<数据统计>
 * <p>
 * createed by luoyingxing on 2020-10-21
 */
public class UsageInfo extends Info {
    private int type;                  //提交数据类型（启动App-1，停止App-2，具体页面-3xxx，）
    private int source;                //提交来源（大众版-1、专业版-2、西科姆版-3）
    private int platform;              //提交平台（安卓-1、苹果-2）
    private String address;            //地理位置


}