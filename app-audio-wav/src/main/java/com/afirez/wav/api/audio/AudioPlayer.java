package com.afirez.wav.api.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by afirez on 18-2-27.
 */

public class AudioPlayer {
    private static final String TAG = "AudioPlayer";

    private static final int DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC;
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    private static final int DEFAULT_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int DEFAULT_MODE = AudioTrack.MODE_STREAM;

    private static final int SAMPLES_PER_FRAME = 1024;

    private volatile boolean isPlayStarted;

    private AudioTrack audioTrack;

    public boolean prepare(){
        return prepare(
                DEFAULT_STREAM_TYPE,
                DEFAULT_SAMPLE_RATE,
                DEFAULT_CHANNEL_CONFIG,
                DEFAULT_FORMAT
        );
    }

    public boolean prepare(
            int streamType,
            int sampleRateInHz,
            int channelConfig,
            int audioFormat) {
        if (isPlayStarted) {
            Log.e(TAG, "Player already started!");
            return false;
        }

        int minBufferSize = AudioTrack.getMinBufferSize(
                sampleRateInHz,
                channelConfig,
                audioFormat
        );

        if (minBufferSize == AudioTrack.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid parameter");
            return false;
        }

        Log.i(TAG, "minBufferSize: " + minBufferSize);
        audioTrack = new AudioTrack(
                streamType,
                sampleRateInHz,
                channelConfig,
                audioFormat,
                minBufferSize,
                DEFAULT_MODE
        );
        if (audioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioTrack initialize failed!");
            return false;
        }

        isPlayStarted = true;

        Log.i(TAG, "startPlay: success!");
        return true;
    }

    public void stopPlay() {
        if (!isPlayStarted || audioTrack == null) {
            return;
        }

        if (audioTrack.getState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.stop();
        }
        audioTrack.release();
        audioTrack = null;
        isPlayStarted = false;
    }

    public boolean play(byte[] audioData, int offset, int count) {
        if (!isPlayStarted || audioTrack == null) {
            Log.e(TAG, "player not started!");
            return false;
        }

        if (audioTrack.write(audioData, offset, count) != count) {
            Log.e(TAG, "could not write all the samples to the audio devices!");
        }
        audioTrack.play();
        Log.d(TAG, "OK, Played " + count + " bytes !");
        return true;
    }
}
