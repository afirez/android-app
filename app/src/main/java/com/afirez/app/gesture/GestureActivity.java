package com.afirez.app.gesture;

import android.content.Context;
import android.media.AudioManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afirez.app.R;

public class GestureActivity extends AppCompatActivity {

    private ConstraintLayout mClRoot;
    private LinearLayout mLlProgress;
    private ImageView mIvIndicator;
    private ProgressBar mPbProgress;

    private MyGestureHelper mMyGestureHelper;

    private AudioManager mAudioManager;
    private BrightnessHelper mBrightnessHelper;
    private Window mWindow;
    private WindowManager.LayoutParams mAttributes;
    private float mScreenBrightness;

    private int mMaxVolume = 0;
    private int mOldVolume = 0;
    private int mNewProgress = 0, mOldProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);
        mClRoot = (ConstraintLayout) findViewById(R.id.cl_root);
        mLlProgress = (LinearLayout) findViewById(R.id.ll_progress);
        mIvIndicator = (ImageView) findViewById(R.id.iv_indicator);
        mPbProgress = (ProgressBar) findViewById(R.id.pb_progress);

        mMyGestureHelper = new MyGestureHelper(mClRoot);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mBrightnessHelper = new BrightnessHelper(this);
        mWindow = getWindow();
        mAttributes = mWindow.getAttributes();
        mScreenBrightness = mAttributes.screenBrightness;
    }

    private class MyGestureHelper extends GestureHelper {

        private static final String TAG = "MyGestureHelper";

        public MyGestureHelper(View view) {
            super(view);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown: ");
            mOldProgress = mNewProgress;
            mOldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mScreenBrightness = mAttributes.screenBrightness;
            if (mScreenBrightness == -1) {
                mScreenBrightness = mBrightnessHelper.getSystemBrightness() / 255f;
            }
            return super.onDown(e);
        }

        @Override
        public void onScrollModeHorizontalComplete(MotionEvent event) {
            Log.d(TAG, "onScrollModeHorizontalComplete: ");
            t("设置进度为" + mNewProgress);
        }

        @Override
        public void onScroll(int scrollMode, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll: " + MotionEvent.ACTION_DOWN + " " + MotionEvent.ACTION_MOVE + " " + MotionEvent.ACTION_UP);
            Log.d(TAG, "onScroll: " + e1.getAction() + " " + e2.getAction());
            switch (scrollMode) {
                case ScrollMode.RIGHT_VERTICAL:
                    int scale = getHeight() / mMaxVolume;
                    int newVolume = (int) ((e1.getY() - e2.getY()) / scale + mOldVolume);
                    mAudioManager.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            newVolume,
                            AudioManager.FLAG_PLAY_SOUND
                    );

                    int progressVolume = (int) (100f * newVolume / mMaxVolume);
                    if (progressVolume > 50) {
                        mIvIndicator.setImageResource(R.drawable.volume_higher_w);
                    } else if (progressVolume > 0) {
                        mIvIndicator.setImageResource(R.drawable.volume_lower_w);
                    } else {
                        mIvIndicator.setImageResource(R.drawable.volume_off_w);
                    }
                    mPbProgress.setProgress(progressVolume);
                    mLlProgress.setVisibility(View.VISIBLE);
                    mLlProgress.removeCallbacks(hideProgressAction);
                    mLlProgress.postDelayed(hideProgressAction, delayMillis);
                    break;
                case ScrollMode.LEFT_VERTICAL:
                    float newBrightness = (e1.getY() - e2.getY()) / getHeight() + mScreenBrightness;
                    if (newBrightness < 0) {
                        newBrightness = 0;
                    } else if (newBrightness > 1) {
                        newBrightness = 1;
                    }
                    mAttributes.screenBrightness = newBrightness;
                    mWindow.setAttributes(mAttributes);
                    mBrightnessHelper.setSystemBrightness((int) (newBrightness * 255));

                    mIvIndicator.setImageResource(R.drawable.brightness_w);
                    mPbProgress.setProgress((int) (newBrightness * 100));
                    mLlProgress.setVisibility(View.VISIBLE);
                    mLlProgress.removeCallbacks(hideProgressAction);
                    mLlProgress.postDelayed(hideProgressAction, delayMillis);
                    break;
                case ScrollMode.HORIZONTAL:
                    float offset = e2.getX() - e1.getX();
                    mNewProgress = (int) (mOldProgress + offset / getHeight() * 100);
                    if (offset > 0) {
                        mIvIndicator.setImageResource(R.drawable.ff);
                        if (mNewProgress > 100) {
                            mNewProgress = 100;
                        }
                    } else {
                        mIvIndicator.setImageResource(R.drawable.fr);
                        if (mNewProgress < 0) {
                            mNewProgress = 0;
                        }
                    }
                    mPbProgress.setProgress(mNewProgress);
                    mLlProgress.setVisibility(View.VISIBLE);
                    mLlProgress.removeCallbacks(hideProgressAction);
                    mLlProgress.postDelayed(hideProgressAction, delayMillis);
                    break;
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling: ");
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.d(TAG, "onShowPress: ");
            super.onShowPress(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress: ");
            super.onLongPress(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp: ");
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed: ");
            t("SingleTap");
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: ");
            t("DoubleTap");
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d(TAG, "onDoubleTapEvent: ");
            return super.onDoubleTapEvent(e);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                t("" + mNewProgress);
            }
            Log.d(TAG, "onTouchEvent: " + event.getAction());
            super.onTouchEvent(event);
        }

        @Override
        public boolean onContextClick(MotionEvent e) {
            Log.d(TAG, "onContextClick: ");
            return super.onContextClick(e);
        }
    }

    private void t(String tip) {
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
    }

    private long delayMillis = 1000;

    private Runnable hideProgressAction = new Runnable() {
        @Override
        public void run() {
            mLlProgress.setVisibility(View.GONE);
        }
    };
}
