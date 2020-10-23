package com.lyx.curl.runnable;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPoolManager
 * <p>
 * author:  luoyingxing
 * date: 2020/10/23.
 */
public class ThreadPoolManager {
    private static volatile ThreadPoolManager mInstance = null;

    public static ThreadPoolManager getInstance() {
        if (null == mInstance) {
            synchronized (ThreadPoolManager.class) {
                if (null == mInstance) {
                    mInstance = new ThreadPoolManager();
                }
            }
        }

        return mInstance;
    }

    private ThreadPoolExecutor executor;

    private ThreadPoolManager() {
        executor = new ThreadPoolExecutor(0,
                50,
                20L,
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    public void execute(Runnable runnable) {
        if (null == runnable) {
            return;
        }

        executor.execute(runnable);
    }

    public void remove(Runnable runnable) {
        if (null == runnable) {
            return;
        }

        executor.remove(runnable);
    }
}