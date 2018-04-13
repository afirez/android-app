package com.win16.bluetoothclass6.connect;

/**
 * 处理网络协议，对数据进行封包或解包
 * Created by Rex on 2015/6/5.
 */
public interface ProtocolHandler<T> {

    byte[] encodePackage(T data);

    T decodePackage(byte[] netData);
}
