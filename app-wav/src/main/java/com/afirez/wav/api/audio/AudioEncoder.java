package com.afirez.wav.api.audio;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 1. createEncoderByType/createDecoderByType
 * 2. configure
 * 3. start
 * 4. while(1) {
 * dequeueInputBuffer
 * queueInputBuffer
 * dequeueOutputBuffer
 * releaseOutputBuffer
 * }
 * 5. stop
 * 6. release
 * <p>
 * Created by afirez on 18-3-20.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class AudioEncoder {

    private static final String TAG = "AudioEncoder";

    private static final String DEFAULT_MIME = "audio/mp4a-latm";
    private static final int DEFAULT_PROFILE_LEVEL = MediaCodecInfo.CodecProfileLevel.AACObjectLC;
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    private static final int DEFAULT_CHANNELS = 1;
    private static final int DEFAULT_BIT_RATE = 128000; //AAC-LC; 64 *1024 for AAC-HE
    private static final int DEFAULT_MAX_BUFFER_SIZE = 16384;

    private String mime;
    private int aacProfile;

    public AudioEncoder() {
        this.mime = DEFAULT_MIME;
        this.aacProfile = DEFAULT_PROFILE_LEVEL;
    }

    private volatile boolean isOpened;

    private volatile MediaCodec encoder;

    public interface OnFrameEncodedListener {
        void onFrameEncoded(byte[] encoded, long presentationTimeUs);
    }

    private OnFrameEncodedListener onFrameEncodedListener;

    public void setOnFrameEncodedListener(OnFrameEncodedListener onFrameEncodedListener) {
        this.onFrameEncodedListener = onFrameEncodedListener;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public boolean open() {
        if (isOpened) {
            return true;
        }
        return open(
          DEFAULT_SAMPLE_RATE,
          DEFAULT_CHANNELS,
          DEFAULT_BIT_RATE,
          DEFAULT_MAX_BUFFER_SIZE
        );
    }

    public boolean open(
            int sampleRate,
            int channels,
            int bitRate,
            int maxBufferSize) {
        Log.i(TAG, "open: sampleRate: " + sampleRate
                + ",channels: " + channels
                + ",bitRate: " + bitRate
                + ",maxBufferSize: " + maxBufferSize
        );

        if (isOpened) {
            return true;
        }

        try {
            encoder = MediaCodec.createEncoderByType(mime);
            MediaFormat format = new MediaFormat();
            format.setString(MediaFormat.KEY_MIME, mime);
            format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, channels);
            format.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate);
            format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, aacProfile);
            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, maxBufferSize);
            encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            encoder.start();
            isOpened = true;
            Log.i(TAG, "open audio encoder success!");
            return true;
        } catch (IOException e) {
            Log.e(TAG, "open audio encoder failed!");
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        if (!isOpened || encoder == null) {
            return;
        }
        Log.i(TAG, "close audio encoder +");
        MediaCodec theEncoder = encoder;
        this.encoder = null;
        theEncoder.stop();
        theEncoder.release();
        isOpened = false;
        Log.i(TAG, "close audio encoder -");
    }

    public synchronized boolean encode(byte[] buffer, long presentationTimeUs) {
        if (!isOpened) {
            return false;
        }
        Log.d(TAG, "encode: presentationTimeUs" + presentationTimeUs);

        try {
            ByteBuffer[] inputBuffers = encoder.getInputBuffers();
            int i = encoder.dequeueInputBuffer(1000);
            if (i > 0) {
                ByteBuffer inputBuffer = inputBuffers[i];
                inputBuffer.clear();
                inputBuffer.put(buffer);
                encoder.queueInputBuffer(i, 0, buffer.length, presentationTimeUs, 0);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }
        Log.d(TAG, "encode -");
        return true;
    }

    public synchronized boolean retrieve() {
        if (!isOpened) {
            return false;
        }
        Log.d(TAG, "retrieve encoded frame +");
        try {
            ByteBuffer[] outputBuffers = encoder.getOutputBuffers();
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int i = encoder.dequeueOutputBuffer(bufferInfo, 1000);
            if (i > 0) {
                Log.d(TAG, "size : " + bufferInfo.size);
                ByteBuffer outputBuffer = outputBuffers[i];
                outputBuffer.position(bufferInfo.offset);
                outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                byte[] theBuffer = new byte[bufferInfo.size];
                outputBuffer.get(theBuffer, 0, bufferInfo.size);
                if (onFrameEncodedListener != null) {
                    onFrameEncodedListener.onFrameEncoded(theBuffer, bufferInfo.presentationTimeUs);
                }
                encoder.releaseOutputBuffer(i, false);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }
        Log.d(TAG, "retrieve encoded frame -");
        return true;
    }
}
