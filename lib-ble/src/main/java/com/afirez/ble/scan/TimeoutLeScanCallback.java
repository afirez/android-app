package com.afirez.ble.scan;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Looper;

import com.afirez.ble.LiteBle;

/**
 * Created by afirez on 2018/4/10.
 */

public abstract class TimeoutLeScanCallback implements BluetoothAdapter.LeScanCallback {

    private Handler handler = new Handler(Looper.getMainLooper());

    private long timeoutMillis;

    public TimeoutLeScanCallback(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    private LiteBle liteBle;

    public LiteBle getLiteBle() {
        return liteBle;
    }

    public TimeoutLeScanCallback setLiteBle(LiteBle liteBle) {
        this.liteBle = liteBle;
        return this;
    }

    public void setupTimer() {
        if (timeoutMillis > 0) {
            removeTimer();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    liteBle.startLeScan(TimeoutLeScanCallback.this);
                    onLeScanTimeout();
                }
            }, timeoutMillis);
        }
    }

    public abstract void onLeScanTimeout();

    public void removeTimer() {
        handler.removeCallbacksAndMessages(null);
    }
}
