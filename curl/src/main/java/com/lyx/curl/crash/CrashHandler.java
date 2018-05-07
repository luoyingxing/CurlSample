package com.lyx.curl.crash;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lyx.curl.R;

/**
 * CrashHandler
 * <p>
 * Autor: luoyingxing
 * Time: 2018/5/7
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Context mContext;
    private static CrashHandler mInstance;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private String mCrashTip;
    /**
     * 提交崩溃日志的URL
     */
    private String mUrl;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (null == mInstance) {
            synchronized (CrashHandler.class) {
                if (mInstance == null) {
                    mInstance = new CrashHandler();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化奔溃日志
     *
     * @param context  上下文对象
     * @param crashTip 若不为控，奔溃时，则Toast提示，若为空，则不提示
     * @param url      需要上传日志的地址
     */
    public void init(Context context, String crashTip, String url) {
        this.mContext = context;
        this.mCrashTip = crashTip;
        this.mUrl = url;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        startExceptionService();
    }

    private void startExceptionService() {
        if (null != mContext) {
            mContext.startService(new Intent(mContext, ExceptionService.class));
        }
    }

    protected String getCrashUrl() {
        return mUrl;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        if (!handleException(e) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, e);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable e) {
        if (null == e) {
            return false;
        }

        final StringBuilder message = new StringBuilder();
        message.append(e.getMessage()).append("\n");
        StackTraceElement[] elements = e.getStackTrace();

        if (null != elements) {
            for (StackTraceElement stack : elements) {
                message.append(stack.toString()).append("\n");
            }
        }

        new Thread() {

            @Override
            public void run() {
                //TODO write exception in file.
                CrashUtils.savePref(mContext, CrashUtils.CRASH_LOG, message.toString());
                CrashUtils.savePref(mContext, CrashUtils.CRASH_TIME, CrashUtils.currentDateTime());
                Looper.prepare();
                showToast();
                Looper.loop();
            }

        }.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        return true;
    }

    private void showToast() {
        if (!TextUtils.isEmpty(mCrashTip)) {
            Toast toast = new Toast(mContext);
            View toastView = LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null);
            ((TextView) toastView.findViewById(R.id.tv_layout_toast)).setText(mCrashTip);
            toast.setView(toastView);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}