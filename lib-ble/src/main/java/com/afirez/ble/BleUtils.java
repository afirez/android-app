package com.afirez.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by afirez on 18-1-22.
 */

public class BleUtils {

    public static final String TAG = "Bluetooth";

    public static void enableBluetooth(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    public static boolean enableBluetooth(Context context) {
        BluetoothManager bm = (BluetoothManager) context.getSystemService(
                Context.BLUETOOTH_SERVICE);
        if (bm == null) {
            return false;
        }
        BluetoothAdapter adapter = bm.getAdapter();
        if (!adapter.isEnabled()) {
            adapter.enable();
        }
        return true;
    }

    public static void printServices(BluetoothGatt gatt) {
        if (gatt == null) {
            return;
        }
        for (BluetoothGattService service : gatt.getServices()) {
            if (service == null) {
                continue;
            }
            Log.i(TAG, "service: " + service.getUuid());
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            if (characteristics == null) {
                continue;
            }
            for (BluetoothGattCharacteristic characteristic : characteristics) {
                if (characteristic == null) {
                    continue;
                }
                Log.d(TAG, "  characteristic: " + characteristic.getUuid() + " value: " + Arrays.toString(characteristic.getValue()));
                List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
                if (descriptors == null) {
                    continue;
                }
                for (BluetoothGattDescriptor descriptor : descriptors) {
                    if (descriptor == null) {
                        continue;
                    }
                    Log.v(TAG, "        descriptor: " + descriptor.getUuid() + " value: " + Arrays.toString(descriptor.getValue()));
                }
            }
        }
    }

    public static void closeGatt(BluetoothGatt gatt) {
        if (gatt == null) {
            return;
        }
        gatt.disconnect();
        refreshGatt(gatt);
        gatt.close();
    }

    public static boolean refreshGatt(BluetoothGatt gatt) {
        try {
            final Method refresh = BluetoothGatt.class.getMethod("refresh");
            if (refresh != null) {
                final boolean success = (Boolean) refresh.invoke(gatt);
                Log.i(TAG, "Refreshing result: " + success);
                return success;
            }
        } catch (Exception e) {
            Log.e(TAG, "An exception occured while refreshing device", e);
        }
        return false;
    }

    public static BluetoothGattService getService(BluetoothGatt gatt, String uuid) {
        if (gatt == null) {
            return null;
        }
        return gatt.getService(UUID.fromString(uuid));
    }

    public static BluetoothGattCharacteristic getCharacteristic(BluetoothGattService service, String uuid) {
        if (service == null) {
            return null;
        }
        return service.getCharacteristic(UUID.fromString(uuid));
    }

    public static BluetoothGattCharacteristic getCharacteristic(BluetoothGatt gatt, String serviceUUID, String characteristicUUID) {
        if (gatt == null) {
            return null;
        }
        BluetoothGattService service = gatt.getService(UUID.fromString(serviceUUID));
        if (service == null) {
            return null;
        }
        return service.getCharacteristic(UUID.fromString(characteristicUUID));
    }
}
