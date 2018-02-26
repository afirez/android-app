package com.afirez.mp3player;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    public static final String URL = "http://http.open.qingting.fm/786/77891.mp3?deviceid=12312&clientid=ZTQ2NTkwNGUtNmM1OS0xMWU3LTkyM2YtMDAxNjNlMDAyMGFk";

    private static final String TAG = "Player";
    private TextView tvBuffering;
    private TextView tvPlay;
    private TextView tvStop;
    private TextView tvMediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBuffering = (TextView) findViewById(R.id.mp_tv_buffering);
        tvPlay = (TextView) findViewById(R.id.mp_tv_play);
        tvStop = (TextView) findViewById(R.id.mp_tv_stop);
        tvMediaPlayer = (TextView) findViewById(R.id.mp_tv_media_player);

        tvMediaPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                release();
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });

        tvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBgHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        doSomeWork();
                    }
                });
            }
        });

        tvStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        showStopped();
    }

    private void showStopped() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvBuffering.setVisibility(View.GONE);
                tvPlay.setVisibility(View.VISIBLE);
                tvStop.setVisibility(View.GONE);
            }
        });
    }

    private void showBuffering() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvBuffering.setVisibility(View.VISIBLE);
                tvPlay.setVisibility(View.GONE);
                tvStop.setVisibility(View.GONE);
            }
        });
    }

    private void showPlaying() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvBuffering.setVisibility(View.GONE);
                tvPlay.setVisibility(View.GONE);
                tvStop.setVisibility(View.VISIBLE);
            }
        });
    }

    private void doSomeWork() {
        showBuffering();
        extractor = new MediaExtractor();
        try {
            extractor.setDataSource(URL);
        } catch (IOException e) {
            Log.e(TAG, "error on extractor.setDataSource: ");
            e.printStackTrace();
            return;
        }

        MediaFormat format = extractor.getTrackFormat(0);
        String mime = format.getString(MediaFormat.KEY_MIME);
        try {
            codec = MediaCodec.createDecoderByType(mime);
        } catch (IOException e) {
            Log.e(TAG, "error on MediaCodec.createDecoderByType: ");
            e.printStackTrace();
            return;
        }
        codec.configure(format, null, null, 0);
        codec.start();

        inputBuffers = codec.getInputBuffers();
        outputBuffers = codec.getOutputBuffers();

        int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        Log.i(TAG, "mime: " + mime);
        Log.i(TAG, "sampleRate: " + sampleRate);

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioTrack.getMinBufferSize(
                        sampleRate,
                        AudioFormat.CHANNEL_OUT_STEREO,
                        AudioFormat.ENCODING_PCM_16BIT
                ),
                AudioTrack.MODE_STREAM
        );

        audioTrack.play();
        extractor.selectTrack(0);

        long timeoutUs = 10000;
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        boolean sawInputEos = false;
        boolean sawOutputEos = false;
        int noOutputCounter = 0;
        int noOutputCounterLimit = 50;


        int sampleSize;
        byte[] chunk = null;
        ByteBuffer inputBuffer;
        ByteBuffer outputBuffer;

        while (!sawOutputEos
                && noOutputCounter < noOutputCounterLimit
                && !stopped) {
            Log.i(TAG, "decode: ");
            noOutputCounter++;
            if (!sawInputEos) {
                inputBufferIndex = codec.dequeueInputBuffer(timeoutUs);
                Log.d(TAG, "codec.dequeueInputBuffer: " + inputBufferIndex);
                bufferIndexCheck++;
                if (inputBufferIndex >= 0) {
                    inputBuffer = inputBuffers[inputBufferIndex];
                    sampleSize = extractor.readSampleData(inputBuffer, 0);
                    Log.d(TAG, "extractor.readSampleData: " + sampleSize);
                    long presentationTimeUs = 0;
                    if (sampleSize < 0) {
                        Log.d(TAG, "input eos: ");
                        sawInputEos = true;
                        sampleSize = 0;
                    } else {
                        presentationTimeUs = extractor.getSampleTime();
                        Log.d(TAG, "extractor.getSampleTime: " + presentationTimeUs);
                    }

                    Log.d(TAG, "codec.queueInputBuffer");
                    codec.queueInputBuffer(
                            inputBufferIndex,
                            0,
                            sampleSize,
                            presentationTimeUs,
                            sawInputEos ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0
                    );

                    if (!sawInputEos) {
                        Log.d(TAG, "extractor.advance");
                        extractor.advance();
                    }
                } else {
                    Log.e(TAG, "inputBufferIndex: " + inputBufferIndex);
                }
            }

            Log.d(TAG, "codec.dequeueOutputBuffer");
            int code = codec.dequeueOutputBuffer(bufferInfo, timeoutUs);

            if (code >= 0) {
                if (bufferInfo.size > 0) {
                    noOutputCounter = 0;
                }

                outputBufferIndex = code;
                outputBuffer = outputBuffers[outputBufferIndex];
                Log.d(TAG, "buffer size: " + bufferInfo.size);
                chunk = new byte[bufferInfo.size];
                outputBuffer.get(chunk);
                outputBuffer.clear();
                if (chunk.length > 0) {
                    Log.d(TAG, "audioTrack.write: " + chunk.length);
                    audioTrack.write(chunk, 0, chunk.length);
                    if (!playing) {
                        playing = true;
                        showPlaying();
                    }
                }
                Log.d(TAG, "codec.releaseOutputBuffer" + outputBufferIndex);
                codec.releaseOutputBuffer(outputBufferIndex, false);
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    Log.d(TAG, "saw output eos.");
                    sawOutputEos = true;
                }
            } else if (code == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                Log.d(TAG, "output format changed: " + codec.getOutputFormat());
            } else if (code == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                outputBuffers = codec.getOutputBuffers();
                Log.d(TAG, "output buffers changed: ");
            } else {
                Log.d(TAG, "mediaCodec.dequeueOutputBuffer returned: " + code);
            }
        }
        Log.d(TAG, "stopping...");
        release();
        showStopped();
    }

    private void stop() {
        stopped = true;
        playing = false;
    }

    private void release() {
        stop();
        if (extractor != null) {
            extractor.release();
            extractor = null;
        }

        if (codec != null) {
            codec.stop();
            codec.release();
            codec = null;
        }

        if (audioTrack != null) {
            audioTrack.flush();
            audioTrack.release();
            audioTrack = null;
        }
    }

    private MediaExtractor extractor;

    private MediaCodec codec;

    private AudioTrack audioTrack;

    private ByteBuffer[] inputBuffers;
    private ByteBuffer[] outputBuffers;
    private int inputBufferIndex;
    private int bufferIndexCheck;
    private int outputBufferIndex;

    private boolean stopped;

    private boolean playing;

    private HandlerThread mHandlerThread;
    private Handler mBgHandler;

    {
        mHandlerThread = new HandlerThread("player");
        mHandlerThread.start();
        mBgHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    protected void onDestroy() {
        release();
        if (mBgHandler != null) {
            mBgHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
