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
 * Created by afirez on 18-3-20.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class AudioDecoder {

    private static final String TAG = "AudioDecoder";

    private static final String DEFAULT_MIME = "audio/mp4a-latm";
    private static final int DEFAULT_PROFILE = MediaCodecInfo.CodecProfileLevel.AACObjectLC;
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    private static final int DEFAULT_CHANNELS = 1;
    private static final int DEFAULT_MAX_BUFFER_SIZE = 16384;

    private String mime;
    private int aacProfile;

    public AudioDecoder() {
        this.mime = DEFAULT_MIME;
        this.aacProfile = DEFAULT_PROFILE;
    }

    public interface OnFrameDecodedListener {
        void onFrameDecoded(byte[] decoded, long presentationTimeUs);
    }

    private OnFrameDecodedListener onFrameDecodedListener;

    public void setOnFrameDecodedListener(OnFrameDecodedListener onFrameDecodedListener) {
        this.onFrameDecodedListener = onFrameDecodedListener;
    }

    private volatile boolean isOpened;

    private volatile MediaCodec decoder;

    private boolean isFirstFrame;

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
                DEFAULT_MAX_BUFFER_SIZE
        );
    }

    public boolean open(
            int sampleRate,
            int channels,
            int maxBufferSize) {
        Log.i(TAG, "open: sampleRate: " + sampleRate
                + ",channels: " + channels
                + ",maxBufferSize: " + maxBufferSize
        );
        if (isOpened) {
            return true;
        }

        try {
            decoder = MediaCodec.createDecoderByType(mime);
            MediaFormat format = new MediaFormat();
            format.setString(MediaFormat.KEY_MIME, mime);
            format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, channels);
            format.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate);
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, aacProfile);
            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, maxBufferSize);
            decoder.configure(format, null, null, 0);
            decoder.start();
            isOpened = true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Log.i(TAG, "open audio decoder success !");
        return true;
    }

    public void close() {
        Log.i(TAG, "close audio decoder +");
        if (!isOpened || decoder == null) {
            Log.i(TAG, "close audio decoder -");
            return;
        }
        MediaCodec theDecoder = decoder;
        decoder = null;
        theDecoder.stop();
        theDecoder.release();
        isOpened = false;
        Log.i(TAG, "close audio decoder -");
    }

    public synchronized boolean decode(byte[] buffer, long presentationTimeUs) {
        Log.d(TAG, "decode: " + presentationTimeUs);
        if (!isOpened) {
            return false;
        }

        try {
            ByteBuffer[] inputBuffers = decoder.getInputBuffers();
            int i = decoder.dequeueInputBuffer(1000);
            if (i > 0) {
                ByteBuffer inputBuffer = inputBuffers[i];
                inputBuffer.clear();
                inputBuffer.put(buffer);
                /**
                 * Some formats, notably AAC audio and MPEG4, H.264 and H.265 video formats
                 * require the actual data to be prefixed by a number of buffers containing
                 * setup data, or codec specific data. When processing such compressed formats,
                 * this data must be submitted to the codec after start() and before any frame data.
                 * Such data must be marked using the flag BUFFER_FLAG_CODEC_CONFIG in a call to queueInputBuffer.
                 */
                int flags = isFirstFrame ? MediaCodec.BUFFER_FLAG_CODEC_CONFIG : 0;
                isFirstFrame = false;
                decoder.queueInputBuffer(
                        i,
                        0,
                        buffer.length,
                        presentationTimeUs,
                        flags
                );
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }

        Log.d(TAG, "decode -");
        return false;
    }

    public synchronized boolean retrieve() {
        Log.d(TAG, "retrieve decoded frame +");
        if (!isOpened) {
            return false;
        }

        try {
            ByteBuffer[] outputBuffers = decoder.getOutputBuffers();
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int i = decoder.dequeueOutputBuffer(bufferInfo, 1000);
            if (i > 0) {
                Log.d(TAG, "size: " + bufferInfo.size);
                ByteBuffer outputBuffer = outputBuffers[i];
                byte[] theBuffer = new byte[bufferInfo.size];
                outputBuffer.position(bufferInfo.offset);
                outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                outputBuffer.get(theBuffer, 0, bufferInfo.size);
                if (onFrameDecodedListener != null) {
                    onFrameDecodedListener.onFrameDecoded(theBuffer, bufferInfo.presentationTimeUs);
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }

        Log.d(TAG, "retrieve decoded frame +");
        return true;
    }
}
