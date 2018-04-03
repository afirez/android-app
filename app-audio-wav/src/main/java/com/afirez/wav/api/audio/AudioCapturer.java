package com.afirez.wav.api.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by afirez on 18-2-27.
 */

public class AudioCapturer {

    private static final String TAG = "AudioCapturer";

    private static final int DEFAULT_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int DEFAULT_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private static final int SAMPLES_PER_FRAME = 1024;

    public interface OnFrameCapturedListener {
        void onFrameCaptured(byte[] buffer);
    }

    private OnFrameCapturedListener onFrameCapturedListener;

    public void setOnAudioFrameCapturedListner(OnFrameCapturedListener onFrameCapturedListener) {
        this.onFrameCapturedListener = onFrameCapturedListener;
    }

    private volatile boolean isCaptureStarted;

    public boolean isCaptureStarted() {
        return isCaptureStarted;
    }

    private AudioRecord audioRecord;

    private Thread captureThread;

    private volatile boolean isLoopExit;

    public boolean startCapture() {
        return startCapture(
                DEFAULT_SOURCE,
                DEFAULT_SAMPLE_RATE,
                DEFAULT_CHANNEL_CONFIG,
                DEFAULT_FORMAT
        );
    }

    public boolean startCapture(
            int audioSource,
            int sampleRateInHz,
            int channelConfig,
            int audioFormat) {
        if (isCaptureStarted) {
            Log.e(TAG, "Captrue already started!");
            return false;
        }

        int minBufferSize = AudioRecord.getMinBufferSize(
                sampleRateInHz,
                channelConfig,
                audioFormat
        );
        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid parameter!");
            return false;
        }
        audioRecord = new AudioRecord(
                audioSource,
                sampleRateInHz,
                channelConfig,
                audioFormat,
                minBufferSize
        );
        if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioRecord initialize failed!");
            return false;
        }

        audioRecord.startRecording();
        isLoopExit = false;

        captureThread = new Thread(new AudioCaptureRunnable());
        captureThread.start();

        isLoopExit = true;
        Log.i(TAG, "Audio capture success!");
        return true;
    }

    public void stopCapture() {
        if (isCaptureStarted) {
            return;
        }

        isLoopExit = true;

        try {
            captureThread.interrupt();
            captureThread.join(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (audioRecord.getState() == AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord.stop();
        }

        audioRecord.release();
        isCaptureStarted = false;
        onFrameCapturedListener = null;

        Log.i(TAG, "stopCapture: success");
    }

    private AudioFrameBuffers mAudioFrameBuffers = new AudioFrameBuffers();

    private class AudioCaptureRunnable implements Runnable {
        @Override
        public void run() {
            while (!isLoopExit) {
                byte[] buffer = mAudioFrameBuffers.obtain(SAMPLES_PER_FRAME * 2);
                int read = audioRecord.read(buffer, 0, buffer.length);
                if (read == AudioRecord.ERROR_INVALID_OPERATION) {
                    Log.e(TAG, "Error: ERROR_INVALID_OPERATION");
                } else if (read == AudioRecord.ERROR_BAD_VALUE) {
                    Log.e(TAG, "Error: ERROR_BAD_VALUE");
                } else {
                    Log.i(TAG, "Audio captured: " + buffer.length);
                    if (onFrameCapturedListener != null) {
                        onFrameCapturedListener.onFrameCaptured(buffer);
                    }
                }
            }
        }
    }
}
