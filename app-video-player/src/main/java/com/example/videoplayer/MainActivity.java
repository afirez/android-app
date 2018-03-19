package com.example.videoplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SurfaceView svVideo;
    private String filePath;
    private SimplePlayer player;
    private TextView tvPlay;
    private TextView tvPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        svVideo = (SurfaceView) findViewById(R.id.vp_sv_video);
        tvPlay = (TextView) findViewById(R.id.vp_tv_play);
        tvPause = (TextView) findViewById(R.id.vp_tv_pause);
        svVideo.getHolder().addCallback(callback);
        tvPlay.setOnClickListener(playOnClickListener);
        tvPause.setOnClickListener(pauseOnClickListener);
    }

    private View.OnClickListener pauseOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (player != null) {
                player.pause();
            }
        }
    };

    private View.OnClickListener playOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (player != null) {
                player.continuePlay();
            }
        }
    };

    private SimplePlayer.IPlayStateListener playStateListener = new SimplePlayer.IPlayStateListener() {
        @Override
        public void videoAspect(int width, int height, float time) {

        }
    };

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player = new SimplePlayer(holder.getSurface(), filePath);
            player.setPlayStateListener(playStateListener);
            player.play();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (player != null) {
                player.destroy();
            }
        }
    };
}
