package com.afirez.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

/**
 * Created by afirez on 18-1-22.
 */

public class BleUtils {

    public static boolean enableBluetoothLe(Context context) {
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
}
