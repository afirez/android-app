package com.afirez.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.MessageQueue;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afirez.gradle.GradleActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_gradle_maven).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGradleMaven(v);
            }
        });
//        Looper.myQueue().addIdleHandler(idleHandler);
    }

    private MessageQueue.IdleHandler idleHandler = new MessageQueue.IdleHandler() {

        private HandlerThread mHandlerThread;

        {
            mHandlerThread = new HandlerThread("bg");
            mHandlerThread.start();
        }

        private volatile long mStartTime = -1;

        private Handler mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                mStartTime = -1;
                switch (msg.what) {
                    case 45:
                        Toast.makeText(MainActivity.this, "45", Toast.LENGTH_SHORT).show();
                        break;
                    case 15:
                        Toast.makeText(MainActivity.this, "15", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        @Override
        public boolean queueIdle() {
            long theTime = System.currentTimeMillis();
            Log.d("idleHandler", "queueIdle: " + theTime);
            if (mStartTime == -1) {
                mStartTime = theTime;
                mHandler.sendEmptyMessageDelayed(45, 2000);
                mHandler.sendEmptyMessageDelayed(15, 5000);
            } else {
                long duration = theTime - mStartTime;
                if (duration < 2000) {
                    mHandler.removeMessages(45);
                    mHandler.removeMessages(15);
                } else if (duration < 5000) {
                    mHandler.removeMessages(15);
                }
                mStartTime = -1;
            }
            return true;
        }
    };

    private volatile boolean flag;

    public void onTime(View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                long startTime = -1;
                for (; ; ) {
                    if (startTime == -1) {
                        startTime = System.currentTimeMillis();
                    } else {
                        long duration = System.currentTimeMillis() - startTime;
                        if (duration > 6000) {
                            break;
                        }
                    }
                }
                Log.d("idleHandler", "run complete: ");
            }
        },1000);
    }

    public void onGradleMaven(View view) {
        Intent intent = new Intent(this, GradleActivity.class);
        startActivity(intent);
    }
}
