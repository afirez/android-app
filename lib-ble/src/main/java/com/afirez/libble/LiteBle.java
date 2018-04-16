package com.afirez.libble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import com.afirez.libble.scan.MacTimeoutLeScanCallback;
import com.afirez.libble.scan.TimeoutLeScanCallback;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by afirez on 2018/4/10.
 */

public class LiteBle {
    private static final String TAG = "Bluetooth";

    public static final int DEFAULT_SCAN_TIMEOUT = 3000;

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_SCANNING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECT_FAILURE = 3;
    public static final int STATE_CONNECTED = 4;
    public static final int STATE_SERVICES_DISCOVERED = 5;

    private volatile int connectionState = STATE_DISCONNECTED;

    public int getConnectionState() {
        return connectionState;
    }

    private Context context;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    public Context getContext() {
        return context;
    }

    public BluetoothManager getBluetoothManager() {
        return bluetoothManager;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    private int scanTimeout = DEFAULT_SCAN_TIMEOUT;

    private LiteBle() {

    }

    private static volatile LiteBle sInstance;

    public static LiteBle getInstance() {
        if (sInstance == null) {
            synchronized (LiteBle.class) {
                if (sInstance == null) {
                    sInstance = new LiteBle();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        this.context = context.getApplicationContext();
        this.bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = this.bluetoothManager.getAdapter();
    }

    private BluetoothGatt gatt;

    public BluetoothGatt getGatt() {
        return gatt;
    }

    public void setGatt(BluetoothGatt gatt) {
        this.gatt = gatt;
    }

    private static final int CALLBACK_CORE = 0;
    private static final int CALLBACK_CONNECT = 1;
    private static final int CALLBACK_DISCOVERY = CALLBACK_CONNECT;
    private static final int CALLBACK_CHARACTERISTIC_CHANGED = 3;
    private static final int CALLBACK_CHARACTERISTIC_READ = 4;
    private static final int CALLBACK_CHARACTERISTIC_WRITE = 5;
    private static final int CALLBACK_DESCRIPTOR_READ = 6;
    private static final int CALLBACK_DESCRIPTOR_WRITE = 7;
    private static final int CALLBACK_RELIABLE_WRITE = 8;
    private static final int CALLBACK_RSSI_READ = 9;
    private static final int CALLBACK_PHY_UPDATE = 10;
    private static final int CALLBACK_PHY_READ = 11;
    private static final int CALLBACK_MTU_CHANGE = 12;

    private final SparseArray<Set<BluetoothGattCallback>> callbacksMap = new SparseArray<>();

    public boolean addCallBack(int callbackType, BluetoothGattCallback callback) {
        boolean result;
        synchronized (callbacksMap) {
            Set<BluetoothGattCallback> gattCallbacks = callbacksMap.get(callbackType);
            if (gattCallbacks == null) {
                gattCallbacks = new LinkedHashSet<>();
            }
            result = gattCallbacks.add(callback);
        }
        return result;
    }

    public synchronized boolean removeCallback(int callbackType, BluetoothGattCallback callback) {
        boolean result;
        synchronized (callbacksMap) {
            Set<BluetoothGattCallback> gattCallbacks = callbacksMap.get(callbackType);
            if (gattCallbacks == null) {
                gattCallbacks = new LinkedHashSet<>();
            }
            result = gattCallbacks.remove(callback);
        }
        return result;
    }

    public boolean isScanning() {
        return connectionState == STATE_SCANNING;
    }

    public boolean isConnectingOrConnected() {
        return connectionState >= STATE_CONNECTING && connectionState != STATE_CONNECT_FAILURE;
    }

    public boolean isConnectFailed() {
        return connectionState == STATE_CONNECT_FAILURE;
    }

    public boolean isConnected() {
        return connectionState >= STATE_CONNECTED;
    }

    public boolean isServicesDiscorved() {
        return connectionState == STATE_SERVICES_DISCOVERED;
    }

    private BluetoothGattCallback dispatcherGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //local dispatch
            Set<BluetoothGattCallback> callbacks = callbacksMap.get(CALLBACK_CONNECT);
            if (callbacks != null && callbacks.size() != 0) {
                for (BluetoothGattCallback callback : callbacks) {
                    if (callback != null) {
                        callback.onConnectionStateChange(gatt, status, newState);
                    }
                }
            }
            //global dispatch
            Set<BluetoothGattCallback> gattCallbacks = callbacksMap.get(CALLBACK_CORE);
            if (gattCallbacks != null && gattCallbacks.size() != 0) {
                for (BluetoothGattCallback gattCallback : gattCallbacks) {
                    if (gattCallback != null) {
                        gattCallback.onConnectionStateChange(gatt, status, newState);
                    }
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Set<BluetoothGattCallback> callbacks = callbacksMap.get(CALLBACK_DISCOVERY);
            if (callbacks != null && callbacks.size() != 0) {
                for (BluetoothGattCallback callback : callbacks) {
                    if (callback != null) {
                        callback.onServicesDiscovered(gatt, status);
                    }
                }
            }
            Set<BluetoothGattCallback> gattCallbacks = callbacksMap.get(CALLBACK_CORE);
            if (gattCallbacks != null && gattCallbacks.size() != 0) {
                for (BluetoothGattCallback gattCallback : gattCallbacks) {
                    if (gattCallback != null) {
                        gattCallback.onServicesDiscovered(gatt, status);
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(
                BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic,
                int status) {
            Set<BluetoothGattCallback> callbacks = callbacksMap.get(CALLBACK_CHARACTERISTIC_READ);
            if (callbacks != null && callbacks.size() != 0) {
                for (BluetoothGattCallback callback : callbacks) {
                    if (callback != null) {
                        callback.onCharacteristicRead(gatt, characteristic, status);
                    }
                }
            }
            Set<BluetoothGattCallback> gattCallbacks = callbacksMap.get(CALLBACK_CORE);
            if (gattCallbacks != null && gattCallbacks.size() != 0) {
                for (BluetoothGattCallback gattCallback : gattCallbacks) {
                    if (gattCallback != null) {
                        gattCallback.onCharacteristicRead(gatt, characteristic, status);
                    }
                }
            }
        }

        @Override
        public void onCharacteristicWrite(
                BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic,
                int status) {
            Set<BluetoothGattCallback> callbacks = callbacksMap.get(CALLBACK_CHARACTERISTIC_WRITE);
            if (callbacks != null && callbacks.size() != 0) {
                for (BluetoothGattCallback callback : callbacks) {
                    if (callback != null) {
                        callback.onCharacteristicWrite(gatt, characteristic, status);
                    }
                }
            }
            Set<BluetoothGattCallback> gattCallbacks = callbacksMap.get(CALLBACK_CORE);
            if (gattCallbacks != null && gattCallbacks.size() != 0) {
                for (BluetoothGattCallback gattCallback : gattCallbacks) {
                    if (gattCallback != null) {
                        gattCallback.onCharacteristicWrite(gatt, characteristic, status);
                    }
                }
            }
        }

        @Override
        public void onCharacteristicChanged(
                BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic) {
            Set<BluetoothGattCallback> callbacks = callbacksMap.get(CALLBACK_CHARACTERISTIC_CHANGED);
            if (callbacks != null && callbacks.size() != 0) {
                for (BluetoothGattCallback callback : callbacks) {
                    if (callback != null) {
                        callback.onCharacteristicChanged(gatt, characteristic);
                    }
                }
            }
            Set<BluetoothGattCallback> gattCallbacks = callbacksMap.get(CALLBACK_CORE);
            if (gattCallbacks != null && gattCallbacks.size() != 0) {
                for (BluetoothGattCallback gattCallback : gattCallbacks) {
                    if (gattCallback != null) {
                        gattCallback.onCharacteristicChanged(gatt, characteristic);
                    }
                }
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Set<BluetoothGattCallback> callbacks = callbacksMap.get(CALLBACK_DESCRIPTOR_READ);
            if (callbacks != null && callbacks.size() != 0) {
                for (BluetoothGattCallback callback : callbacks) {
                    if (callback != null) {
                        callback.onDescriptorRead(gatt, descriptor, status);
                    }
                }
            }
            Set<BluetoothGattCallback> gattCallbacks = callbacksMap.get(CALLBACK_CORE);
            if (gattCallbacks != null && gattCallbacks.size() != 0) {
                for (BluetoothGattCallback gattCallback : gattCallbacks) {
                    if (gattCallback != null) {
                        gattCallback.onDescriptorRead(gatt, descriptor, status);
                    }
                }
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Set<BluetoothGattCallback> callbacks = callbacksMap.get(CALLBACK_DESCRIPTOR_WRITE);
            if (callbacks != null && callbacks.size() != 0) {
                for (BluetoothGattCallback callback : callbacks) {
                    if (callback != null) {
                        callback.onDescriptorWrite(gatt, descriptor, status);
                    }
                }
            }
            Set<BluetoothGattCallback> gattCallbacks = callbacksMap.get(CALLBACK_CORE);
            if (gattCallbacks != null && gattCallbacks.size() != 0) {
                for (BluetoothGattCallback gattCallback : gattCallbacks) {
                    if (gattCallback != null) {
                        gattCallback.onDescriptorWrite(gatt, descriptor, status);
                    }
                }
            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            Set<BluetoothGattCallback> callbacks = callbacksMap.get(CALLBACK_RELIABLE_WRITE);
            if (callbacks != null && callbacks.size() != 0) {
                for (BluetoothGattCallback callback : callbacks) {
                    if (callback != null) {
                        callback.onReliableWriteCompleted(gatt, status);
                    }
                }
            }
            Set<BluetoothGattCallback> gattCallbacks = callbacksMap.get(CALLBACK_CORE);
            if (gattCallbacks != null && gattCallbacks.size() != 0) {
                for (BluetoothGattCallback gattCallback : gattCallbacks) {
                    if (gattCallback != null) {
                        gattCallback.onReliableWriteCompleted(gatt, status);
                    }
                }
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Set<BluetoothGattCallback> callbacks = callbacksMap.get(CALLBACK_RSSI_READ);
            if (callbacks != null && callbacks.size() != 0) {
                for (BluetoothGattCallback callback : callbacks) {
                    if (callback != null) {
                        callback.onReadRemoteRssi(gatt, rssi, status);
                    }
                }
            }
            Set<BluetoothGattCallback> gattCallbacks = callbacksMap.get(CALLBACK_CORE);
            if (gattCallbacks != null && gattCallbacks.size() != 0) {
                for (BluetoothGattCallback gattCallback : gattCallbacks) {
                    if (gattCallback != null) {
                        gattCallback.onReadRemoteRssi(gatt, rssi, status);
                    }
                }
            }
        }

        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            if (Build.VERSION.SDK_INT >= 26) {
                Set<BluetoothGattCallback> callbacks = callbacksMap.get(CALLBACK_PHY_UPDATE);
                if (callbacks != null && callbacks.size() != 0) {
                    for (BluetoothGattCallback callback : callbacks) {
                        if (callback != null) {
                            callback.onPhyUpdate(gatt, txPhy, rxPhy, status);
                        }
                    }
                }
                Set<BluetoothGattCallback> gattCallbacks = callbacksMap.get(CALLBACK_CORE);
                if (gattCallbacks != null && gattCallbacks.size() != 0) {
                    for (BluetoothGattCallback gattCallback : gattCallbacks) {
                        if (gattCallback != null) {
                            gattCallback.onPhyUpdate(gatt, txPhy, rxPhy, status);
                        }
                    }
                }
            }
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            if (Build.VERSION.SDK_INT >= 26) {
                Set<BluetoothGattCallback> callbacks = callbacksMap.get(CALLBACK_PHY_READ);
                if (callbacks != null && callbacks.size() != 0) {
                    for (BluetoothGattCallback callback : callbacks) {
                        if (callback != null) {
                            callback.onPhyRead(gatt, txPhy, rxPhy, status);
                        }
                    }
                }
                Set<BluetoothGattCallback> gattCallbacks = callbacksMap.get(CALLBACK_CORE);
                if (gattCallbacks != null && gattCallbacks.size() != 0) {
                    for (BluetoothGattCallback gattCallback : gattCallbacks) {
                        if (gattCallback != null) {
                            gattCallback.onPhyRead(gatt, txPhy, rxPhy, status);
                        }
                    }
                }
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            if (Build.VERSION.SDK_INT >= 21) {
                Set<BluetoothGattCallback> callbacks = callbacksMap.get(CALLBACK_PHY_READ);
                if (callbacks != null && callbacks.size() != 0) {
                    for (BluetoothGattCallback callback : callbacks) {
                        if (callback != null) {
                            callback.onMtuChanged(gatt, mtu, status);
                        }
                    }
                }
                Set<BluetoothGattCallback> gattCallbacks = callbacksMap.get(CALLBACK_CORE);
                if (gattCallbacks != null && gattCallbacks.size() != 0) {
                    for (BluetoothGattCallback gattCallback : gattCallbacks) {
                        if (gattCallback != null) {
                            gattCallback.onMtuChanged(gatt, mtu, status);
                        }
                    }
                }
            }
        }
    };

    public boolean startLeScan(BluetoothAdapter.LeScanCallback scanCallback) {
        if (scanCallback == null) {
            return false;
        }
        boolean success = bluetoothAdapter.startLeScan(scanCallback);
        if (success) {
            connectionState = STATE_SCANNING;
        }
        return success;
    }

    public boolean startLeScan(TimeoutLeScanCallback scanCallback) {
        if (scanCallback == null) {
            return false;
        }
        scanCallback.setLiteBle(this).setupTimer();
        boolean success = bluetoothAdapter.startLeScan(scanCallback);
        if (success) {
            connectionState = STATE_SCANNING;
        } else {
            scanCallback.removeTimer();
        }
        return success;
    }

    public void stopLeScan(BluetoothAdapter.LeScanCallback scanCallback) {
        if (scanCallback == null) {
            return;
        }
        if (scanCallback instanceof TimeoutLeScanCallback) {
            ((TimeoutLeScanCallback) scanCallback).removeTimer();
        }
        bluetoothAdapter.stopLeScan(scanCallback);
        if (connectionState == STATE_SCANNING) {
            connectionState = STATE_DISCONNECTED;
        }
    }

    public synchronized BluetoothGatt connect(
            BluetoothDevice device,
            boolean autoConnect,
            BluetoothGattCallback connectCallback) {
        Set<BluetoothGattCallback> connectCallbacks = callbacksMap.get(CALLBACK_CONNECT);
        if (connectCallbacks == null) {
            connectCallbacks = new LinkedHashSet<>();
        }
        connectCallbacks.add(connectCallback);
        return device.connectGatt(context, autoConnect, dispatcherGattCallback);
    }

    public boolean scanAndConnect(
            String mac,
            final boolean autoConnect,
            final BluetoothGattCallback connectCallback) {
        if (!BluetoothAdapter.checkBluetoothAddress(mac)) {
            return false;
        }
        startLeScan(new MacTimeoutLeScanCallback(mac, scanTimeout) {
            @Override
            public void onDeviceFound(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connect(device, autoConnect, connectCallback);
                    }
                });
            }

            @Override
            public void onLeScanTimeout() {
                if (dispatcherGattCallback != null) {
                    dispatcherGattCallback.onConnectionStateChange(gatt, BluetoothGatt.GATT_FAILURE, BluetoothGatt.STATE_DISCONNECTED);
                }
            }
        });
        return true;
    }

    public boolean refreshGatt() {
        try {
            Method refresh_Method = BluetoothGatt.class.getMethod("refresh");
            boolean success = (Boolean) refresh_Method.invoke(getGatt());
            Log.i(TAG, "refreshGatt: " + success);
            return success;
        } catch (Throwable e) {
            Log.e(TAG, "refreshGatt: ", e);
        }
        return false;
    }

    public void closeGatt() {
        if (gatt != null) {
            gatt.disconnect();
            refreshGatt();
            gatt.close();
            Log.i(TAG, "closeGatt");
        }
    }

    public void enableBluetoothIfDisabled(Activity activity, int requestCode) {
        if (!bluetoothAdapter.isEnabled()) {
            BleUtils.enableBluetooth(activity, requestCode);
        }
    }

    public void enableBluetooth(Activity activity, int requestCode) {
        BleUtils.enableBluetooth(activity, requestCode);
    }

    public void enableBluetooth() {
        bluetoothAdapter.enable();
    }

    public void disableBluetooth() {
        bluetoothAdapter.disable();
    }

    private Handler handler = new Handler(Looper.getMainLooper());

    public void runOnUiThread(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        }
        if (handler != null) {
            handler.post(runnable);
        }
    }
}
