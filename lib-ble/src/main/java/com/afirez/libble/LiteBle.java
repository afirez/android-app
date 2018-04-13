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
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_SERVICES_DISCOVERED = 4;

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

    private Set<BluetoothGattCallback> gattCallbacks = new LinkedHashSet<>();

    public boolean addGattCallBack(BluetoothGattCallback gattCallback) {
        return gattCallbacks.add(gattCallback);
    }

    public boolean removeGattCallback(BluetoothGattCallback gattCallback) {
        return gattCallbacks.remove(gattCallback);
    }

    public boolean isScanning() {
        return connectionState == STATE_SCANNING;
    }

    public boolean isConnectingOrConnected() {
        return connectionState >= STATE_CONNECTING;
    }

    public boolean isConnected() {
        return connectionState >= STATE_CONNECTED;
    }

    public boolean isServicesDiscorved() {
        return connectionState == STATE_SERVICES_DISCOVERED;
    }

    private BluetoothGattCallback coreGattCallbalk = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                connectionState = STATE_CONNECTED;
                LiteBle.this.onConnected(gatt, status);
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                connectionState = STATE_DISCONNECTED;
                LiteBle.this.onDisconnected(gatt, status);
            }
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
            LiteBle.this.onServicesDiscovered(gatt, status);
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
            LiteBle.this.onCharacteristicRead(gatt, characteristic, status);
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
            LiteBle.this.onCharacteristicWrite(gatt, characteristic, status);
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
            LiteBle.this.onCharacteristicChanged(gatt, characteristic);
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
            LiteBle.this.onDescriptorRead(gatt, descriptor, status);
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
            LiteBle.this.onDescriptorWrite(gatt, descriptor, status);
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
            LiteBle.this.onReliableWriteCompleted(gatt, status);
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
            LiteBle.this.onReadRemoteRssi(gatt, rssi, status);
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
                LiteBle.this.onPhyUpdate(gatt, txPhy, rxPhy, status);
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
                LiteBle.this.onPhyRead(gatt, txPhy, rxPhy, status);
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
                LiteBle.this.onMtuChanged(gatt, mtu, status);
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

    private void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {

    }

    private void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {

    }

    private void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {

    }

    private void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

    }

    private void onReliableWriteCompleted(BluetoothGatt gatt, int status) {

    }

    private void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

    }

    private void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

    }

    private void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

    }

    private void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    private void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    private void onServicesDiscovered(BluetoothGatt gatt, int status) {

    }

    private void onDisconnected(BluetoothGatt gatt, int status) {

    }

    private void onConnected(BluetoothGatt gatt, int status) {

    }

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
            BluetoothGattCallback gattCallback) {
        gattCallbacks.add(gattCallback);
        return device.connectGatt(context, autoConnect, coreGattCallbalk);
    }

    public boolean scanAndConnect(
            String mac,
            final boolean autoConnect,
            final BluetoothGattCallback gattCallback) {
        if (!BluetoothAdapter.checkBluetoothAddress(mac)) {
            return false;
        }
        startLeScan(new MacTimeoutLeScanCallback(mac, scanTimeout) {
            @Override
            public void onDeviceFound(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connect(device, autoConnect, gattCallback);
                    }
                });
            }

            @Override
            public void onLeScanTimeout() {
                if (gattCallback != null) {
                    gattCallback.onConnectionStateChange(gatt, BluetoothGatt.GATT_SUCCESS, BluetoothGatt.STATE_DISCONNECTED);
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
