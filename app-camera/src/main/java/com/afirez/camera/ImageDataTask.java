package com.afirez.camera;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ImageDataTask {
    private static final int KEEP_ALIVE_TIME = 10;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private BlockingQueue<Runnable> workQueue;
    private ThreadPoolExecutor executor;
    private LinkedBlockingQueue<Data> queue = new LinkedBlockingQueue<>();

    public ImageDataTask() {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        int maximumPoolSize = corePoolSize * 2;
        workQueue = new LinkedBlockingQueue<>();
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, KEEP_ALIVE_TIME, TIME_UNIT, workQueue);
    }

    public boolean offer(Data data) {
        return queue.offer(data);
    }

    public synchronized void post(final Data data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                onPreProcess(data);
                onProcess(data);
                onPostProcess(data);
            }
        });
    }

    private void onPostProcess(Data data) {
        if (callback != null) {
            callback.onPostProcess(data);
        }
    }

    private void onProcess(Data data) {
        if (callback != null) {
            callback.onProcess(data);
        }
    }

    private void onPreProcess(Data data) {
        if (callback != null) {
            callback.onPreProcess(data);
        }
    }

    public static class Data {

        public Data(byte[] buffer, int width, int height, int size) {
            this.buffer = buffer;
            this.width = width;
            this.height = height;
            this.size = size;
        }

        public byte[] buffer;
        public int width;
        public int height;
        public int size;
    }

    private Callback callback;

    public interface Callback {
        void onPostProcess(Data data);

        void onProcess(Data data);

        void onPreProcess(Data data);
    }
}
