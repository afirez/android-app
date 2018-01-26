package com.example.app_battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BATTERY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = registerReceiver(null, filter);
        if (intent != null) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            Log.d(TAG, String.valueOf(level / scale));

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_UNKNOWN);
            String batteryStatus;
            switch (status) {
                case BatteryManager.BATTERY_STATUS_FULL:
                    batteryStatus = "已充满";
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    batteryStatus = "充电中";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    batteryStatus = "放电中";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    batteryStatus = "未充电";
                    break;
                default:
                    batteryStatus = "状态未知";
                    break;
            }

            Log.d(TAG, batteryStatus);

            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            String batteryPlugged;
            switch (plugged) {
                case BatteryManager.BATTERY_PLUGGED_AC:
                    batteryPlugged = "使用充电器充电";
                    break;
                case BatteryManager.BATTERY_PLUGGED_USB:
                    batteryPlugged = "使用USB充电";
                    break;
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    batteryPlugged = "使用无线方式充电";
                    break;
                default:
                    batteryPlugged = "未知充电方式";
                    break;
            }
            Log.d(TAG, batteryPlugged);
        } else {
            Log.e(TAG, "failed");
        }

        IntentFilter connectionFilter = new IntentFilter();
        connectionFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        connectionFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(receiver, connectionFilter);

        IntentFilter dockedFilter = new IntentFilter(Intent.ACTION_DOCK_EVENT);
        Intent dockedState = registerReceiver(null, dockedFilter);
        if (dockedState != null) {
            int dockState = dockedState.getIntExtra(Intent.EXTRA_DOCK_STATE, -1); //底座类型
            boolean isDocked = dockState != Intent.EXTRA_DOCK_STATE_UNDOCKED; //是否插入了底座

            switch (dockState) {
                case Intent.EXTRA_DOCK_STATE_CAR:
                    //车载底座
                    break;
                case Intent.EXTRA_DOCK_STATE_DESK:
                    //桌面底座
                    break;
                case Intent.EXTRA_DOCK_STATE_LE_DESK:
                    //低端（模拟）桌面基座 API >= 11
                    break;
                case Intent.EXTRA_DOCK_STATE_HE_DESK:
                    //高端（数字）桌面基座 API >= 11
                    break;
            }
        }
    }

    private PowerConnectionReceiver receiver = new PowerConnectionReceiver();

    public static class PowerConnectionReceiver extends BroadcastReceiver {

        private static final String TAG = "PowerConnectionReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
                Log.d(TAG, "power connected");
            } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
                Log.e(TAG, "power disconnected");
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
