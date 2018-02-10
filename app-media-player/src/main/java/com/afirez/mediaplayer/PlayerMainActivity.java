package com.afirez.mediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlayerMainActivity extends AppCompatActivity {

    private static final String TAG = "MP";
    private TextView tvTime;
    private MusicTimeReceiver mReceiver;
    private TextView tvPlayOrPause;
    private TextView tvPrevious;
    private TextView tvNext;
    private RecyclerView rvSongs;
    private volatile boolean mIsPlaying;
    private SeekBar sbProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_main);
        initViews();

        mReceiver = new MusicTimeReceiver();
        registerReceiver(
                mReceiver,
                new IntentFilter(MusicTimeReceiver.ACTION_TIME)
        );
    }

    private boolean mIsSeeking;

    private int mProgress;

    private void initViews() {
        sbProgress = (SeekBar) findViewById(R.id.mp_sb_progress);
        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgress = progress;
                int duration = seekBar.getMax();
                date.setTime(duration);
                String durationText = sdf.format(date);
                date.setTime(progress);
                String progressText = sdf.format(date);
                tvTime.setText(progressText + "/" + durationText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsSeeking = false;
                if (mEntities.isEmpty()) {
                    return;
                }

                if (mIsPlaying) {
                    currentPosition = (currentPosition + mAdapter.getItemCount() - 1) % mAdapter.getItemCount();
                    if (currentPosition < 0) {
                        return;
                    }
                    currentPosition = currentPosition + 1 % mAdapter.getItemCount();
                    Intent intent = new Intent(
                            PlayerMainActivity.this,
                            MusicPlayerService.class
                    );
                    intent.putExtra("path", mEntities.get(currentPosition).getAbsolutePath());
                    intent.putExtra("operation", "seek");
                    intent.putExtra("progress", mProgress);
                    startService(intent);
                }
            }
        });

        tvTime = (TextView) findViewById(R.id.mp_tv_time);
        tvTime.setText("00:00/00:00");
        tvPlayOrPause = (TextView) findViewById(R.id.mp_tv_play_or_pause);
        tvPlayOrPause.setText(mIsPlaying ? "暂停" : "播放");
        tvPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEntities.isEmpty()) {
                    return;
                }
                mIsPlaying = !mIsPlaying;
                Intent intent = new Intent(
                        PlayerMainActivity.this,
                        MusicPlayerService.class
                );
                intent.putExtra("path", mEntities.get(currentPosition).getAbsolutePath());
                intent.putExtra("isPlaying", mIsPlaying);
                intent.putExtra("operation", "playOrPause");
                intent.putExtra("progress", mProgress);
                startService(intent);
                tvPlayOrPause.setText(mIsPlaying ? "暂停" : "播放");
            }
        });

        tvPrevious = (TextView) findViewById(R.id.mp_tv_previous);
        tvPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEntities.isEmpty()) {
                    return;
                }
                currentPosition = (currentPosition + mAdapter.getItemCount() - 1) % mAdapter.getItemCount();
                if (currentPosition < 0) {
                    return;
                }
                currentPosition = currentPosition + 1 % mAdapter.getItemCount();
                Intent intent = new Intent(
                        PlayerMainActivity.this,
                        MusicPlayerService.class
                );
                intent.putExtra("path", mEntities.get(currentPosition).getAbsolutePath());
                intent.putExtra("operation", "previous");
                intent.putExtra("progress", mProgress);
                startService(intent);
                mIsPlaying = true;
                tvPlayOrPause.setText("暂停");
            }
        });

        tvNext = (TextView) findViewById(R.id.mp_tv_next);
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEntities.isEmpty()) {
                    return;
                }
                currentPosition = currentPosition + 1 % mAdapter.getItemCount();
                Intent intent = new Intent(
                        PlayerMainActivity.this,
                        MusicPlayerService.class
                );
                intent.putExtra("path", mEntities.get(currentPosition).getAbsolutePath());
                intent.putExtra("operation", "next");
                intent.putExtra("progress", mProgress);
                startService(intent);
                mIsPlaying = true;
                tvPlayOrPause.setText("暂停");
            }
        });
        rvSongs = (RecyclerView) findViewById(R.id.mp_rv_songs);
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        mEntities = new ArrayList<>();
        mAdapter = new Adapter(mEntities);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        rvSongs.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        songs();
    }

    private void songs() {
        Log.d(TAG, "dir: " + musicDir.getAbsolutePath());
        mFiles = new ArrayList<>();
        File[] files = musicDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (mFileFilter.accept(musicDir, file.getName())) {
                mFiles.add(file);
            }
        }
        mEntities.clear();
        mEntities.addAll(mFiles);
        mAdapter.notifyDataSetChanged();
    }


    private File musicDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MUSIC
    );

    private List<File> mFiles;

    private FilenameFilter mFileFilter = new Mp3FileFilter();

    private static class Mp3FileFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name != null && name.endsWith(".mp3");
        }
    }

    private List<File> mEntities;

    private Adapter mAdapter;

    private int currentPosition;

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            if (currentPosition == position && mIsPlaying) {
                return;
            }
            currentPosition = position;
            Intent intent = new Intent(
                    PlayerMainActivity.this,
                    MusicPlayerService.class
            );
            intent.putExtra("path", mEntities.get(currentPosition).getAbsolutePath());
            intent.putExtra("operation", "random");
            intent.putExtra("progress", mProgress);
            startService(intent);
            mIsPlaying = true;
            tvPlayOrPause.setText("暂停");
        }
    };

    private interface OnItemClickListener {
        void onItemClick(int position);
    }

    private static class Adapter extends RecyclerView.Adapter<VH> {

        private List<File> mEntities;

        public Adapter(List<File> entities) {
            mEntities = entities;
        }

        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        private LayoutInflater mInflater;

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.getContext());
            }
            View view = mInflater.inflate(R.layout.mp_item_song, parent, false);
            return new VH(view, mOnItemClickListener);
        }

        private MediaMetadataRetriever mMetadataRetriever;

        @Override
        public void onBindViewHolder(VH holder, int position) {
            File entity = mEntities.get(position);
            holder.tvName.setText(entity.getName());
            if (mMetadataRetriever == null) {
                mMetadataRetriever = new MediaMetadataRetriever();
            }
            mMetadataRetriever.setDataSource(entity.getAbsolutePath());
            String author = mMetadataRetriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_AUTHOR);
            holder.tvAuthor.setText(TextUtils.isEmpty(author) ? "不知名歌手" : author);
        }

        @Override
        public int getItemCount() {
            return mEntities == null ? 0 : mEntities.size();
        }
    }

    private static class VH extends RecyclerView.ViewHolder {
        private final OnItemClickListener mOnItemClickListener;
        private final TextView tvName;
        private final TextView tvAuthor;

        public VH(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            mOnItemClickListener = onItemClickListener;
            tvName = ((TextView) itemView.findViewById(R.id.mp_tv_song_name));
            tvAuthor = ((TextView) itemView.findViewById(R.id.mp_tv_song_author));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.CHINA);

    private Date date = new Date();

    public class MusicTimeReceiver extends BroadcastReceiver {

        public static final String ACTION_TIME = "com.afirez.mp.time";


        @Override
        public void onReceive(Context context, Intent intent) {
            int duration = intent.getIntExtra("duration", 0);
            int progress = intent.getIntExtra("progress", 0);
            if (progress == duration) {
                mProgress = 0;
                progress = 0;
                mIsPlaying = false;
                tvPlayOrPause.setText("播放");
            }

            if (!mIsSeeking) {
                date.setTime(duration);
                String durationText = sdf.format(date);
                date.setTime(progress);
                String progressText = sdf.format(date);
                tvTime.setText(progressText + "/" + durationText);
                sbProgress.setMax(duration);
                sbProgress.setProgress(progress);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }
}
