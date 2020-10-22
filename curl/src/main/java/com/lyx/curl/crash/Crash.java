package com.lyx.curl.crash;

import com.lyx.curl.usage.Info;

/**
 * Crash 奔溃日志信息
 * <p>
 * author:  luoyingxing
 * date: 2018/4/24.
 */
public class Crash extends Info {
    /**
     * exception : Java NullException...(奔溃日志)
     */
    private String exception;
    /**
     * 生成信息时间
     */
    private String time;

    public Crash() {
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}