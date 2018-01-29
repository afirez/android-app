package com.afirez.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

/**
 * Created by afirez on 18-1-26.
 */

public class BatteryHelper {

    private static final String TAG = "BatteryHelper";

    public interface OnBatteryChangeListener {
        void onBatteryChanged(int percent);
    }

    public interface OnPowerConnectionChangeListener {
        void onPowerConnectionChanged(boolean connected);
    }

    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
    }

    private OnBatteryChangeListener mOnBatteryChangeListener;

    private OnPowerConnectionChangeListener mOnPowerConnectionChangeListener;

    public void setOnBatteryChangeListener(
            OnBatteryChangeListener onBatteryChangeListener) {
        mOnBatteryChangeListener = onBatteryChangeListener;
    }

    public void setOnPowerConnectionChangeListener(
            OnPowerConnectionChangeListener onPowerConnectionChangeListener) {
        mOnPowerConnectionChangeListener = onPowerConnectionChangeListener;
    }


    public void start() {
        sContext.registerReceiver(receiver(), filter());
    }

    public void stop() {
        if (receiver != null) {
            BatteryReceiver temp = this.receiver;
            this.receiver = null;
            filter = null;
            sContext.unregisterReceiver(temp);
        }
    }

    private volatile BatteryReceiver receiver;

    private volatile IntentFilter filter;


    private BatteryReceiver receiver() {
        if (receiver != null) {
            return receiver;
        }
        receiver = new BatteryReceiver();
        return receiver;
    }

    private IntentFilter filter() {
        if (filter != null) {
            return filter;
        }
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        return filter;
    }


    public class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int percent = level / scale;
                Log.d(TAG, String.valueOf(percent));
                if (mOnBatteryChangeListener != null) {
                    mOnBatteryChangeListener.onBatteryChanged(percent);
                }
                return;
            }

            if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
                Log.d(TAG, "power connected");
                if (mOnPowerConnectionChangeListener != null) {
                    mOnPowerConnectionChangeListener.onPowerConnectionChanged(true);
                }
            } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
                Log.e(TAG, "power disconnected");
                if (mOnPowerConnectionChangeListener != null) {
                    mOnPowerConnectionChangeListener.onPowerConnectionChanged(false);
                }
            }
        }
    }
}
