package com.afirez.ble.scan;

import android.bluetooth.BluetoothDevice;

import com.afirez.ble.LiteBle;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lenovo on 2018/4/10.
 */

public abstract class MacTimeoutLeScanCallback extends TimeoutLeScanCallback {
    private String mac;

    private AtomicBoolean aHasFound = new AtomicBoolean(false);

    public MacTimeoutLeScanCallback(String mac, long timeoutMillis) {
        super(timeoutMillis);
        this.mac = mac;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (!aHasFound.get()) {
            if (device.getAddress().equalsIgnoreCase(mac)) {
                aHasFound.set(true);
                LiteBle liteBle = getLiteBle();
                if (liteBle != null) {
                    liteBle.stopLeScan(MacTimeoutLeScanCallback.this);
                    onDeviceFound(device, rssi, scanRecord);
                }
            }
        }
    }

    public abstract void onDeviceFound(BluetoothDevice device, int rssi, byte[] scanRecord);
}
