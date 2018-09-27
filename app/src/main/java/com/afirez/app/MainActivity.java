package com.afirez.app;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import com.afirez.app.cache.RxCacheActivity;

public class MainActivity extends AppCompatActivity {

    private GestureDetector mDetector;
    private int mDisplayWidth;
    private int mDisplayHeight;
    private ViewConfiguration mViewConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        float value = 255.0f;
        mDetector = new GestureDetector(this, mOnGestureListener);
        mDetector.setIsLongpressEnabled(true);

        mViewConfiguration = ViewConfiguration.get(this);

        Display display = getWindowManager().getDefaultDisplay();
        mDisplayWidth = display.getWidth();
        mDisplayHeight = display.getHeight();
        findViewById(R.id.cl_root).setOnTouchListener(new View.OnTouchListener() {
            float oldRawX;
            float oldRawY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                Log.d("screen", "event = " + action);
                if (action == MotionEvent.ACTION_DOWN) {
                    oldRawX = event.getRawX();
                    oldRawY = event.getRawY();
                    return true;
                } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                    return false;
                }
                float rawY = event.getRawY();
                float rawX = event.getRawX();
                float deltaRawY = oldRawY - rawY;
                float deltaRawX = oldRawX - rawX;
                float absDeltaX = Math.abs(deltaRawX);
                float absDeltaY = Math.abs(deltaRawY);
                if (absDeltaX < absDeltaY
                        && oldRawX < mDisplayWidth / 2.0f) {
                    oldRawX = event.getRawX();
                    oldRawY = event.getRawY();
                    onBrightnessSlide(deltaRawY / mDisplayHeight);
                    return true;
                }
                oldRawX = event.getRawX();
                oldRawY = event.getRawY();
                return false;
            }
        });

    }

    private GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        float oldRawX;
        float oldRawY;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (oldRawX == 0) {
                oldRawX = e2.getRawX();
                oldRawY = e2.getRawY();
                return true;
            }
            float rawY = e2.getRawY();
            float rawX = e2.getRawX();
            float deltaRawY = oldRawY - rawY;
            float deltaRawX = oldRawX - rawX;
            float absDeltaX = Math.abs(deltaRawX);
            float absDeltaY = Math.abs(deltaRawY);
            if (absDeltaX < absDeltaY
                    && oldRawX < mDisplayWidth / 2.0f) {
                oldRawX = e2.getRawX();
                oldRawY = e2.getRawY();
                onBrightnessSlide(-deltaRawY / mDisplayHeight);
                return true;
            }
            oldRawX = e2.getRawX();
            oldRawY = e2.getRawY();
            return true;
        }
    };

    private void onBrightnessSlide(float delta) {
        Log.d("screen", "delta: "+ delta);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        float screenBrightness = attributes.screenBrightness;
        screenBrightness = screenBrightness + delta;
        if (screenBrightness > 1f) {
            screenBrightness = 1f;
        } else if (screenBrightness < 0.01f) {
            screenBrightness = 0.01f;
        }
        Log.d("screen", "screenBrightness: " + screenBrightness);
        attributes.screenBrightness = screenBrightness;
        getWindow().setAttributes(attributes);
    }

    private int screenBrightnessMode = -1;

    private int screenBrightness = -1;


    private void stopAutoScreenBrightnessMode() {
        Settings.System.putInt(
                getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onRxCache(View view) {


//        hanoi(3, 'A', 'B', 'C');

        Intent intent = new Intent(this, RxCacheActivity.class);
        startActivity(intent);
    }

    private void hanoi(int n, char x, char y, char z) {
        if (n == 1) {
            move(x, 1, z);
        } else {
            hanoi(n - 1, x, z, y);
            move(x, n, z);
            hanoi(n - 1, y, x, z);
        }
    }

    private void move(char src, int i, char des) {
        Log.d("hanoi", String.format("move[ %d ]: %s ==> %s", i, src, des));
    }
}
