package com.arcsoft.sdk_demo.afirez;

import android.support.v4.util.Pools;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FrameTask {
    private static final int KEEP_ALIVE_TIME = 10;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private BlockingQueue<Runnable> workQueue;
    private ThreadPoolExecutor executor;

    public FrameTask() {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        int maximumPoolSize = corePoolSize * 2;
        workQueue = new LinkedBlockingQueue<>();
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, KEEP_ALIVE_TIME, TIME_UNIT, workQueue);
    }

    public synchronized void post(final byte[] buffer) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                onPreProcess(buffer);
                onProcess(buffer);
                onPostProcess(buffer);
            }
        });
    }

    public void cancel() {
        executor.shutdownNow();
    }

    private void onPostProcess(byte[] buffer) {
        if (callback != null) {
            callback.onPostFrame(buffer);
        }
    }

    private void onProcess(byte[] buffer) {
        if (callback != null) {
            callback.onFrame(buffer);
        }
    }

    private void onPreProcess(byte[] buffer) {
        recycleData(buffer);
        if (callback != null) {
            callback.onPreFrame(buffer);
        }
    }


    private final Pools.Pool<byte[]> pool = new Pools.SynchronizedPool<>(30);

    public byte[] obtainData(int size) {
        byte[] data;
        data = pool.acquire();
        if (data == null || data.length != size) {
            data =new byte[size];
        }
        return data;
    }

    public void recycleData(byte[] data) {
        try {
            pool.release(data);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onPostFrame(byte[] buffer);

        void onFrame(byte[] buffer);

        void onPreFrame(byte[] buffer);
    }
}
