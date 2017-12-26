package com.afirez.anr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvClickMe = (TextView) findViewById(R.id.tv_click_me);
        View decorView = getWindow().getDecorView();
        tvClickMe.setOnClickListener(onTvClickMeClickListener);
        new Thread(runnable).start();
    }

    @Override
    protected void onPause() {
        bgHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    private Handler bgHandler;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            Looper.prepare();
            bgHandler = new Handler(Looper.myLooper());
            bgHandler.post(showAction);
            Looper.loop();
        }
    };

    private ProgressDialog dialog;

    private Runnable showAction = new Runnable() {
        @Override
        public void run() {
            if (dialog == null) {
                dialog = new ProgressDialog(MainActivity.this);
//        dialog.setTitle("test dialog");
                dialog.setMessage("runOnUiThread 会直接将数据放入 looper");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
            }
            dialog.show();
            if (bgHandler != null) {
                bgHandler.postDelayed(hideAction, 3000);
            }
        }
    };

    private Runnable hideAction = new Runnable() {
        @Override
        public void run() {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    };

    private View.OnClickListener onTvClickMeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(
                    MainActivity.this,
                    "Activity.runOnUiThread 会直接将数据放入 looper," +
                            "View.post 会需要 AttachInfo ," +
                            "这个在 OnCreate 里面还没创建出来，所以真正执行的时候会靠后",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, AnrActivity.class);
            startActivity(intent);
        }
    };
}
