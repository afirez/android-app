package com.afirez.mp3player;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class Main2Activity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    public static final String URL = "http://http.open.qingting.fm/786/77891.mp3?deviceid=12312&clientid=ZTQ2NTkwNGUtNmM1OS0xMWU3LTkyM2YtMDAxNjNlMDAyMGFk";

    private static final String TAG = "HP";

    private TextView tvBuffering;
    private TextView tvPlay;
    private TextView tvStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        tvBuffering = (TextView) findViewById(R.id.mp_tv_buffering);
        tvPlay = (TextView) findViewById(R.id.mp_tv_play);
        tvStop = (TextView) findViewById(R.id.mp_tv_stop);

        tvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSomeWork();
            }
        });

        tvStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });
        showStopped();
        doSomeWork();
    }

    private void stop() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private MediaPlayer player;

    private void doSomeWork() {
        showBuffering();
        player = new MediaPlayer();
        player.reset();
        try {
            player.setDataSource(URL);
        } catch (IOException e) {
            e.printStackTrace();
            showStopped();
            return;
        }
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.prepareAsync();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        showPlaying();
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();
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

    @Override
    protected void onDestroy() {
        if (player != null) {
            player.release();
        }
        super.onDestroy();
    }
}
