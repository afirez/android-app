package com.afirez.anr;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class AnrActivity extends AppCompatActivity {

    private static final String TAG = "AnrActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anr);
        TextView tvDialog = (TextView) findViewById(R.id.tv_dialog);
        tvDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                testAnr();
            }
        }).start();
        SystemClock.sleep(10);
        initView();
    }

    private synchronized void initView() {
        Log.d(TAG, "initView: ");
    }

    private void testAnr() {
        Log.d(TAG, "testAnr: 1");
        SystemClock.sleep(35 * 1000);
        Log.d(TAG, "testAnr: 0");
    }

    private ProgressDialog mDialog;

    private ProgressDialog provideDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
//        dialog.setTitle("test dialog");
            mDialog.setMessage("testAnr");
            mDialog.setIndeterminate(true);
            mDialog.setCancelable(false);
        }
        return mDialog;
    }


    private Toast mToast;

    private void tip(String tip) {
        if (mToast == null) {
            mToast = Toast.makeText(this.getApplicationContext(),
                    "", Toast.LENGTH_SHORT);
            View view = mToast.getView().findViewById(android.R.id.message);
            if (view != null) {
                ((TextView) view).setTextSize(28);
            }
        }
        mToast.setText(tip);
        mToast.show();
    }
}
