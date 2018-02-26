package com.example.app_audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView tvRecord;
    private TextView tvPlay;
    private TextView tvTips;


    private Handler bgHandler;
    private Handler uiHandler;

    private static final String AUDIO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "audio" + File.separator + "audio.pcm";
    private static final int BUFFER_SIZE = 2048;

    private volatile boolean isRecording;
    private byte[] buffer;
    private File audioFile;
    private FileOutputStream fos;
    private AudioRecord audioRecord;
    private long startRecordTime;
    private long stopRecordTime;


    private volatile boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buffer = new byte[BUFFER_SIZE];

        HandlerThread handlerThread = new HandlerThread(
                "bg",
                Process.THREAD_PRIORITY_AUDIO
        );
        handlerThread.start();
        bgHandler = new Handler(handlerThread.getLooper());
        uiHandler = new Handler(Looper.getMainLooper());

        tvRecord = (TextView) findViewById(R.id.audio_tv_record);
        tvPlay = (TextView) findViewById(R.id.audio_tv_play);
        tvTips = (TextView) findViewById(R.id.audio_tv_tips);

        tvRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    isRecording = false;
                    tvRecord.setText(R.string.stop_record);
                } else {
                    isRecording = true;
                    tvRecord.setText(R.string.start_record);
                    bgHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!startRecord()) {
                                onStartRecordError();
                            }
                        }
                    });
                }
            }
        });
        tvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioFile != null && !isPlaying) {
                    isPlaying = true;
                    bgHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (startPlay(audioFile)) {
                                onPlayError();
                            }
                            isPlaying = false;
                        }
                    });
                }
            }
        });
    }

    private void onPlayError() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "播放失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean startPlay(File audioFile) {
        int streamType = AudioManager.STREAM_MUSIC;
        int sampleRate = 44100;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int mode = AudioTrack.MODE_STREAM;
        int minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                channelConfig,
                audioFormat
        );
        AudioTrack audioTrack = new AudioTrack(
                streamType,
                sampleRate,
                channelConfig,
                audioFormat,
                Math.max(BUFFER_SIZE, minBufferSize),
                mode
        );

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(audioFile);
            int read;
            while ((read = fis.read(buffer)) > 0) {
                int code = audioTrack.write(buffer, 0, read);
                switch (code) {
                    case AudioTrack.ERROR_INVALID_OPERATION:
                    case AudioTrack.ERROR_BAD_VALUE:
                    case AudioTrack.ERROR_DEAD_OBJECT:
                    case AudioTrack.ERROR:
                        return false;
                    default:
                        break;
                }
            }
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                audioTrack.stop();
                audioTrack.release();
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
    }

    private void onStartRecordError() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "录音失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean startRecord() {
        audioFile = new File(AUDIO_PATH);
        try {
            if (!audioFile.exists() && !audioFile.createNewFile()) {
                return false;
            }
            fos = new FileOutputStream(audioFile);

            int audioSource = MediaRecorder.AudioSource.MIC;
            int sampleRate = 44100;
            int channelConfig = AudioFormat.CHANNEL_IN_MONO;
            int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
            int minBufferSize = AudioRecord.getMinBufferSize(
                    sampleRate,
                    channelConfig,
                    audioFormat
            );
            audioRecord = new AudioRecord(
                    audioSource,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    Math.max(minBufferSize, BUFFER_SIZE)
            );
            audioRecord.startRecording();
            startRecordTime = System.currentTimeMillis();
            int read;
            while (isRecording) {
                read = audioRecord.read(buffer, 0, BUFFER_SIZE);
                if (read > 0) {
                    fos.write(buffer, 0, read);
                } else {
                    return false;
                }
            }
            return stopRecord();
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        } finally {
            if (audioRecord != null) {
                audioRecord.release();
            }
        }
    }

    private boolean stopRecord() {
        try {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
            if (fos != null) {
                fos.close();
            }
            stopRecordTime = System.currentTimeMillis();
            final int interval = (int) (stopRecordTime - startRecordTime);
            if (interval > 3) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvTips.setText(tvTips.getText() + "\n录制成功 " + interval + " 秒");
                    }
                });
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bgHandler != null) {
            bgHandler.removeCallbacksAndMessages(null);
        }
    }
}
