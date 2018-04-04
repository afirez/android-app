package com.afirez.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyBle";

    private BluetoothAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "不支持低功耗蓝牙", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (manager == null) {
            finish();
            return;
        }
        adapter = manager.getAdapter();
        if (adapter != null && !adapter.isEnabled()) {
            adapter.enable();
        }
        if (adapter != null && adapter.isEnabled()) {
            Handler bleHandler = bleHandler();
            bleHandler.postDelayed(stopScanRunnable, scanTimeout);
            bleHandler.post(startScanRunnable);
        }
    }

    private volatile boolean scanning;

    private static final long scanTimeout = 18 * 1000;

    private BluetoothDevice device;

    private Runnable startScanRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "run: ");
            if (!scanning && leScanCallback != null) {
                scanning = true;
                Log.i(TAG, "startScan: thread: " + Thread.currentThread().getName() + (Thread.currentThread() == bleHandler().getLooper().getThread()));
                adapter.startLeScan(leScanCallback);
            }
        }
    };

    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (scanning && leScanCallback != null) {
                Log.i(TAG, "stopScan: thread: " + Thread.currentThread().getName() + (Thread.currentThread() == bleHandler().getLooper().getThread()));
                adapter.stopLeScan(leScanCallback);
                scanning = false;
            }
        }
    };

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Thread thread = Thread.currentThread();
            Log.i(TAG, "onLeScan: thread:" + thread.getName() + (thread == bleHandler().getLooper().getThread()));
            if (device == null || device.getName() == null) {
                Toast.makeText(
                        MainActivity.this,
                        "未扫描到设备",
                        Toast.LENGTH_SHORT).show();
                Log.i(TAG, "未扫描到设备");
                return;
            }
            Toast.makeText(
                    MainActivity.this,
                    "扫描到" + device.getName(),
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "扫描到" + device.getName());
            MainActivity.this.device = device;

            connectGatt(device);
        }
    };

    BluetoothGatt gatt;

    private void connectGatt(final BluetoothDevice device) {
        Handler bleHandler = bleHandler();
        bleHandler.post(stopScanRunnable);
        bleHandler.post(connectGattRunnable);
    }

    private Runnable connectGattRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "connectGatt: thread: " + Thread.currentThread().getName());
            if (device == null) {
                return;
            }
            gatt = device.connectGatt(
                    MainActivity.this,
                    false,
                    gattCallback);
        }
    };

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, final int newState) {
            // binder thread
            Thread thread = Thread.currentThread();
            Log.i(TAG, "onConnectionStateChange: thread:" + thread.getName() + (thread == bleHandler().getLooper().getThread()));
            Handler bleHandler = bleHandler();
            MainActivity.this.newState = newState;
            bleHandler.post(onConnectionStateChangedRunnable);
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // binder thread
            Thread thread = Thread.currentThread();
            Log.i(TAG, "onConnectionStateChange: thread:" + thread.getName() + (thread == bleHandler().getLooper().getThread()));
            if (status != BluetoothGatt.GATT_SUCCESS) {
//                Toast.makeText(
//                        MainActivity.this,
//                        "未发现服务",
//                        Toast.LENGTH_SHORT).show();
                Log.i(TAG, "未发现服务");
            }

//            Toast.makeText(
//                    MainActivity.this,
//                    "发现服务了",
//                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "发现服务了");
            List<BluetoothGattService> services = gatt.getServices();
            Log.i(TAG, "onServicesDiscovered: " + services);
        }
    };

    private volatile int newState;

    private Runnable onConnectionStateChangedRunnable = new Runnable() {

        @Override
        public void run() {
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Toast.makeText(
                        MainActivity.this,
                        "连接已断开",
                        Toast.LENGTH_SHORT).show();
                Log.i(TAG, "连接已断开");
                return;
            }
            if (newState == BluetoothProfile.STATE_DISCONNECTING) { //
                Toast.makeText(
                        MainActivity.this,
                        "断开连接中",
                        Toast.LENGTH_SHORT).show();
                Log.i(TAG, "断开连接中");
                return;
            }
            if (newState == BluetoothProfile.STATE_CONNECTING) {
                Toast.makeText(
                        MainActivity.this,
                        "连接中",
                        Toast.LENGTH_SHORT).show();
                Log.i(TAG, "连接中");
                return;
            }
            Toast.makeText(
                    MainActivity.this,
                    "连接成功",
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "连接成功");
            if (gatt != null) {
                Log.i(TAG, "discoverServices");
                gatt.discoverServices();
            }
        }
    };

    private Handler bleHandler;
    private final Object bleLock = new Object();

    private Handler bleHandler() {
        if (bleHandler == null) {
            synchronized (bleLock) {
                if (bleHandler == null) {
                    HandlerThread thread = new HandlerThread(
                            "ble",
                            Process.THREAD_PRIORITY_BACKGROUND);
                    thread.start();
                    bleHandler = new Handler(thread.getLooper());
                }
            }
        }
        return bleHandler;
    }

    @Override
    protected void onDestroy() {
        if (bleHandler != null) {
            bleHandler.removeCallbacksAndMessages(null);
        }
        if (gatt != null) {
            gatt.disconnect();
            gatt.close();
            gatt = null;
        }
        if (adapter != null && adapter.isEnabled()) {
            adapter.disable();
        }
        super.onDestroy();
    }
}
