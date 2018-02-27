package com.afirez.mp3player;

import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class Main3Activity extends AppCompatActivity implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnSeekCompleteListener {

    public static final String URL = "http://http.open.qingting.fm/786/77891.mp3?deviceid=12312&clientid=ZTQ2NTkwNGUtNmM1OS0xMWU3LTkyM2YtMDAxNjNlMDAyMGFk";

    private static final String TAG = "ijk";
    private IjkMediaPlayer mPlayer;

    private TextView tvBuffering;
    private TextView tvPlay;
    private TextView tvStop;

    private Handler bgHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        HandlerThread handlerThread = new HandlerThread("bg", Process.THREAD_PRIORITY_AUDIO);
        handlerThread.start();
        bgHandler = new Handler(handlerThread.getLooper());

        tvBuffering = (TextView) findViewById(R.id.mp_tv_buffering);
        tvPlay = (TextView) findViewById(R.id.mp_tv_play);
        tvStop = (TextView) findViewById(R.id.mp_tv_stop);

        tvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bgHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startPlay();
                    }
                });
            }
        });

        tvStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bgHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        release();
                        showStopped();
                    }
                });
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

    private void startPlay() {
        showBuffering();
        mPlayer = new IjkMediaPlayer();
        try {
            mPlayer.setDataSource(URL);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnErrorListener(this);
            mPlayer.setOnBufferingUpdateListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnInfoListener(this);
            mPlayer.setOnSeekCompleteListener(this);
            mPlayer.prepareAsync();
        } catch (Throwable e) {
            e.printStackTrace();
            release();
        }
    }

    private void release() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        Log.d(TAG, "onPrepared: ");
        iMediaPlayer.start();
        showPlaying();
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        Log.d(TAG, "onError: " + i + " " + i1);
        showStopped();
        return true;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
        Log.d(TAG, "onBufferingUpdate: " + i);
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        Log.d(TAG, "onCompletion: ");
        showStopped();
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        Log.d(TAG, "onInfo: " + i + " " + i1);
        return true;
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        Log.d(TAG, "onSeekComplete: ");
    }

    @Override
    protected void onDestroy() {
        bgHandler.post(new Runnable() {
            @Override
            public void run() {
                bgHandler.removeCallbacksAndMessages(null);
                release();
            }
        });
        super.onDestroy();
    }
}
