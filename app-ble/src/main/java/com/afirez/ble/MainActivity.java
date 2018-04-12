package com.afirez.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.opengl.ETC1;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Bluetooth";

    public static final String UUID_SERVICE = "0003cdd0-0000-1000-8000-00805f9b0131";
    public static final String UUID_CHARACTERISTIC_NOTIFY = "0003cdd1-0000-1000-8000-00805f9b0131";
    // interval : 10-20ms
    public static final String UUID_CHARACTERISTIC_WRITE_NO_RESPONSE = "0003cdd2-0000-1000-8000-00805f9b0131";

    private BluetoothAdapter adapter;
    private EditText etAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etAddress = (EditText) findViewById(R.id.ble_et_address);

        findViewById(R.id.ble_tv_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = etAddress.getText().toString().trim();
                connectDevice(address);
            }
        });
        findViewById(R.id.ble_tv_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
    }

    private void connectDevice(String address) {
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            Log.i(TAG, "connectDevice: address == null");
            return;
        }
        if (adapter == null) {
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Log.i(TAG, "startScan: no ble");
                return;
            }
            BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (manager == null) {
                Log.i(TAG, "startScan: manager == null");
                return;
            }
            adapter = manager.getAdapter();
        }
        if (adapter == null) {
            Log.i(TAG, "startScan: adapter == null");
            return;
        }
        if (gatt != null) {
            Log.i(TAG, "gatt.close: ");
            gatt.disconnect();
            gatt.close();
            gatt = null;
        }
        if (!adapter.isEnabled()) {
            Log.i(TAG, "startScan: enable ble");
            adapter.enable();
        }
        device = adapter.getRemoteDevice(address);
        bleHandler().post(connectGattRunnable);
    }

    private void startScan() {
        if (adapter == null) {
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Log.i(TAG, "startScan: no ble");
                return;
            }
            BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (manager == null) {
                Log.i(TAG, "startScan: no ble");
                return;
            }
            adapter = manager.getAdapter();
        }
        if (adapter == null) {
            Log.i(TAG, "startScan: no ble");
            return;
        }
//        if (adapter.isEnabled()) {
//            Log.i(TAG, "startScan: disable ble");
//            adapter.disable();
//        }
        if (gatt != null) {
            gatt.close();
            gatt = null;
//            adapter.disable();
        }
        Log.i(TAG, "run: isDiscovering: " + adapter.isDiscovering());
        if (!adapter.isEnabled()) {
            Log.i(TAG, "startScan: enable ble");
            adapter.enable();
            Log.i(TAG, "run: isDiscovering: " + adapter.isDiscovering());
        }
        if (adapter != null && adapter.isEnabled()) {
            Handler bleHandler = bleHandler();
            bleHandler.postDelayed(stopScanRunnable, SCAN_TIMEOUT);
            bleHandler.post(startScanRunnable);
        }
    }

    private volatile boolean scanning;

    private static final long SCAN_TIMEOUT = 3 * 1000;

    private BluetoothDevice device;

    private Runnable startScanRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "run: ");
            if (adapter != null && !scanning && leScanCallback != null) {
                scanning = true;
                Log.i(TAG, "run: isDiscovering: " + adapter.isDiscovering());
                Log.i(TAG, "startScan: thread: " + Thread.currentThread().getName() + (Thread.currentThread() == bleHandler().getLooper().getThread()));
                adapter.startLeScan(leScanCallback);
            }
        }
    };

    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (adapter != null && scanning && leScanCallback != null) {
                Log.i(TAG, "run: isDiscovering: " + adapter.isDiscovering());
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
            if (device == null) {
                Log.i(TAG, "onLeScan: no device");
                return;
            }
            String name = device.getName();
            name = TextUtils.isEmpty(name) ? "device" : name;
            Log.i(TAG, "onLeScan: found: " + name + ": " + device.getAddress());
            MainActivity.this.device = device;
            connectGatt(device);
        }
    };

    private volatile BluetoothGatt gatt;

    private void connectGatt(final BluetoothDevice device) {
        Handler bleHandler = bleHandler();
        bleHandler.post(stopScanRunnable);
        bleHandler.post(connectGattRunnable);
    }

    private Runnable connectGattRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "connectGatt: thread: " + Thread.currentThread().getName());
            if (device != null) {
                if (gatt != null) {
                    gatt.disconnect();
                    gatt.close();
                    gatt = null;
                }
                gatt = device.connectGatt(MainActivity.this, false, gattCallback);
                try {
                    Field mHandler_Field = BluetoothGatt.class.getField("mHandler");
                    mHandler_Field.setAccessible(true);
                    mHandler_Field.set(gatt, bleHandler());
                } catch (Throwable e) {
                    Log.e(TAG, "mHandler_Field: ", e);
                }
            }
        }
    };

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, final int newState) {
            Log.i(TAG, "onConnectionStateChange: op : " + (status == BluetoothGatt.GATT_SUCCESS ? "success" : "failed"));
            Thread thread = Thread.currentThread();
            Log.i(TAG, "onConnectionStateChange: thread:" + thread.getName());
            Handler bleHandler = bleHandler();
            MainActivity.this.newState = newState;
            bleHandler.post(onConnectionStateChangedRunnable);
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "onConnectionStateChange: op : " + (status == BluetoothGatt.GATT_SUCCESS ? "success" : "failed"));
            Thread thread = Thread.currentThread();
            Log.i(TAG, "onConnectionStateChange: thread:" + thread.getName());
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "未发现服务");
                return;
            }
            Log.i(TAG, "发现服务了");
            bleHandler().post(onServicesDiscoveredRunnable);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.i(TAG, "onCharacteristicChanged: ");
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.i(TAG, "onCharacteristicRead: ");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.i(TAG, "onDescriptorWrite: ");
        }
    };

    private volatile int newState;

    private Runnable onConnectionStateChangedRunnable = new Runnable() {

        @Override
        public void run() {
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "连接已断开");
                return;
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "连接成功");
                if (gatt != null) {
                    Log.i(TAG, "discoverServices");
                    gatt.discoverServices();
                }
            }
        }
    };

    private Runnable onServicesDiscoveredRunnable = new Runnable() {
        @Override
        public void run() {
            BleUtils.printServices(gatt);
            if (gatt != null) {
                BluetoothGattService service = gatt.getService(UUID.fromString(UUID_SERVICE));
                if (service != null) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(UUID_CHARACTERISTIC_NOTIFY));
                    if (characteristic != null) {
                        boolean succeed = gatt.setCharacteristicNotification(characteristic, true);
                        if (succeed) {
                            List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
                            if (descriptors != null) {
                                for (BluetoothGattDescriptor descriptor : descriptors) {
                                    if (descriptor != null) {
                                        succeed = descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                                        if (succeed) {
                                            gatt.writeDescriptor(descriptor);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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

//7300-7313 I: onLeScan: found: BLE-Glucowell: 68:9A:E1:10:01:6C
//        7300-7331 I: stopScan: thread: bletrue
//        7300-7331 I: connectGatt: thread: ble
//        7300-7319 I: onConnectionStateChange: thread:Binder_3false
//        7300-7331 I: 连接成功
//        7300-7331 I: discoverServices
//        7300-7312 I: onConnectionStateChange: thread:Binder_1false
//        7300-7312 I: 发现服务了
//        7300-7312 I: onServicesDiscovered: 00001800-0000-1000-8000-00805f9b34fb: android.bluetooth.BluetoothGattService@4207e240
//        7300-7312 I: onServicesDiscovered: 00001801-0000-1000-8000-00805f9b34fb: android.bluetooth.BluetoothGattService@4207e820
//        7300-7312 I: onServicesDiscovered: 0000180a-0000-1000-8000-00805f9b34fb: android.bluetooth.BluetoothGattService@4207edb8
//        7300-7312 I: onServicesDiscovered: 0003cdd0-0000-1000-8000-00805f9b0131: android.bluetooth.BluetoothGattService@4207f350
//        7300-7312 I: onServicesDiscovered: 0000fee7-0000-1000-8000-00805f9b34fb: android.bluetooth.BluetoothGattService@4207f8e8