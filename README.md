# Android Bluetooth 4.0 低功耗蓝牙开发

## 基本概念

### Android 中的低功耗蓝牙

> Android 中的蓝牙分两种：
> - 经典蓝牙，Bluetooth 3.0 以下；
> - 低功耗蓝牙 (BLE)，Bluetooth 4.0.
> 二者本质上没有太多区别，可以理解为后者为前者的升级优化版本。
> 但是 API 上的实现区别还是很大。

经典蓝牙不是本文讨论的话题，我们将讨论低功耗蓝牙开发。
Android 4.3 ( API 18 ) 开始支持 BLE API 。
低功耗蓝牙较传统蓝牙有以下优点：

- 传输速度快
- 覆盖范围广
- 安全性更高
- 延迟更短
- 耗电极低

### Android 中的低功耗蓝牙协议

Android 中的 BLE 协议如下：

- BluetoothGATT
  - BluetoothGATTService
    - BluetoothGattCharacteristic
      - Value
      - BluetoothGattDescriptor
        - Value

> 说明：
> - 一个 BLE 设备包含多个 Service ;
> - 一个 Service 包含多个 Characteristic ；
> - 一个 Characteristic 包含一个 Value 和多个 Descriptor；
> - 一个 Descriptor 包含一个 Value ；
> 通信数据一般存储在 Characteristic 中，目前一个 Characteristic 中 最多能存储 20 byte 。
> 与 Characteristic 相关的权限字段主要有 READ 、WRITE 、WRITE_NO_RESPONSE 和 NOTIFY 等权限字段。

## Android 中的 BLE 开发流程

### 权限配置

- 使用蓝牙需要配置权限

```
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```

- Android 5.0 ( API 21 ) 扫描蓝牙需要定位权限， 否则扫描不到设备。
 实际使用时候发现 5.0 不需要也可以扫描，Android 6.0 ( API 23 ) 以上必须配置。

 ```
 <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
 <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 ```

### 声明特征， 或代码判断

```
// 如果为true表示只能在支持低功耗蓝牙的设备上使用
<uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
// 如果 android:required="false"，采用代码判断
if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
    Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
    finish();
}
```

### 拿到蓝牙适配器

一个 Android 系统只有一个 BluetoothAdapter

```
BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

BluetoothAdapter adapter = manager.getAdapter();
```

### 打开蓝牙

```
if (!bluetoothAdapter.isEnabled()) {
     bluetoothAdapter.enable();
}
```

### 扫描蓝牙

> 扫描蓝牙是比较耗资源的,所以扫描一段时间后应该及时关闭扫面

```
handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                }
            }, 10000);

            scanning = true;
            //需要参数 BluetoothAdapter.LeScanCallback(返回的扫描结果)
            bluetoothAdapter.startLeScan(leScanCallback);
```
### 实现扫描结果的回调

实现上一步开始扫描蓝牙 bluetoothAdapter.startLeScan(leScanCallback) 中的 leScanCallback

```
private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

      @Override
      public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
          if (bluetoothDevice != null && bluetoothDevice.getName() != null) {
              // 扫描到蓝牙设备 作进一步处理
          } else {
              //"没有获取到设备信息";
          }
      }
};
```

### 连接蓝牙

> 连接时应关闭扫描，连接是通过获取到设备的mac地址进行连接的

```
if (scanning) {
    bluetoothAdapter.stopLeScan(leScanCallback);
    scanning = false;
}

BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address());

//第二个参数 是否要自动连接
bluetoothGatt = device.connectGatt(MainActivity.this, false, bluetoothGattCallback);
```

### 实现连接蓝牙的状态回调接口

实现上一步中 device.connectGatt(MainActivity.this, false, bluetoothGattCallback) 中的 bluetoothGattCallback 回调接口,
重写 onConnectionStateChange 方法, 连接状态将通过此方法回调.

```
private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {//连接成功
                Message message = new Message();
                message.what = CONNECT_SUCCESS;
                handler.sendMessage(message);

                //连接成功后去发现该连接的设备的服务
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {//连接失败 或者连接断开都会调用此方法
                Message message = new Message();
                message.what = CONNECT_FAILED;
                handler.sendMessage(message);
            }
        }
}
```

### 重写发现服务的回调方法

连接成功后紧接着就得去发现连接设备中的所有服务 Service .
继续重写 BluetoothGattCallback 中的 onServicesDiscovered 方法

```
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {//发现该设备的服务
                    //拿到该服务
                    // 1,通过UUID拿到指定的服务
                    // 2,可以拿到该设备上所有服务的集合
                    List<BluetoothGattService> serviceList = mBluetoothGatt.getServices();

                    //通过服务可以拿到该服务的UUID，和该服务里的所有属性 Characteristic

                    Message message = new Message();
                    message.what = FOUND_SERVICE;
                    handler.sendMessage(message);
                } else {//未发现该设备的服务

                }
            }
```