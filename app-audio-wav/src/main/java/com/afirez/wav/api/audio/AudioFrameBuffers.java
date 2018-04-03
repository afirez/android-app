package com.afirez.wav.api.audio;

import android.support.v4.util.Pools;

/**
 * Created by afirez on 2018/3/23.
 */

public class AudioFrameBuffers {
     private Pools.SynchronizedPool<byte[]> bufferPool = new Pools.SynchronizedPool<>(10);

     public byte[] obtain(int bufferSize) {
         byte[] buffer = bufferPool.acquire();
         if (buffer == null || buffer.length != bufferSize) {
             buffer = new byte[bufferSize];
         }
         return buffer;
     }

     public boolean recycle(byte[] buffer) {
         try {
             return bufferPool.release(buffer);
         } catch (Throwable e) {
             e.printStackTrace();
             return false;
         }
     }
}
