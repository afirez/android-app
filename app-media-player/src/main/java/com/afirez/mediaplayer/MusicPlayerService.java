package com.afirez.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.text.TextUtils;

import java.io.IOException;

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {


    public MusicPlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String operation = intent.getStringExtra("operation");
        if ("playOrPause".equals(operation)) {
            playOrPause(intent);
        } else if ("previous".equals(operation)
                || "next".equals(operation)
                || "random".equals(operation)) {
            newMusic(intent);
        } else if ("seek".equals(operation)) {
            seek(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void seek(Intent intent) {
        int progress = intent.getIntExtra("progress", 0);
        if (mPlayer == null || !mPlayer.isPlaying()) {
            return;
        }
        mPlayer.seekTo(progress);
        mPlayer.start();
    }

    private int progress;

    private void playOrPause(Intent intent) {
        boolean isPlaying = intent.getBooleanExtra("isPlaying", false);
        String path = intent.getStringExtra("path");
        progress = intent.getIntExtra("progress", 0);
        if (isPlaying) {
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
                mPlayer.reset();
                try {
                    mPlayer.setDataSource(path);
                    mPlayer.setOnCompletionListener(this);
                    mPlayer.setOnPreparedListener(this);
                    mPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            mPlayer.seekTo(progress);
            mPlayer.start();
        } else {
            if (mPlayer == null || !mPlayer.isPlaying()) {
                return;
            }
            mPlayer.pause();
        }
    }

    private volatile MediaPlayer mPlayer;

    private void newMusic(Intent intent) {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        String path = intent.getStringExtra("path");
        progress = intent.getIntExtra("progress", 0);
        mPlayer = new MediaPlayer();
        mPlayer.reset();
        try {
            mPlayer.setDataSource(path);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnPreparedListener(this);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.seekTo(progress);
        mp.start();
        if (musicTimeUpdateRunnable == null) {
            musicTimeUpdateRunnable = new MusicTimeUpdateRunnable();
        }
        new Thread(musicTimeUpdateRunnable).start();
    }

    private MusicTimeUpdateRunnable musicTimeUpdateRunnable;

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();
        mPlayer = null;
        Intent intent = new Intent("com.afirez.mp.time");
        intent.putExtra("duration", mp.getDuration());
        intent.putExtra("progress", mp.getCurrentPosition());
        sendBroadcast(intent);
    }

    private class MusicTimeUpdateRunnable implements Runnable {

        @Override
        public void run() {
            if (mPlayer == null) {
                return;
            }
            while (mPlayer.isPlaying()) {
                int duration = mPlayer.getDuration();
                int progress = mPlayer.getCurrentPosition();
                Intent intent = new Intent("com.afirez.mp.time");
                intent.putExtra("duration", duration);
                intent.putExtra("progress", progress);
                sendBroadcast(intent);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
