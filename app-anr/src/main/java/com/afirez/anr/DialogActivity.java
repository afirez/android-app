package com.afirez.anr;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        TextView tvDialog = (TextView) findViewById(R.id.tv_dialog);
        View decorView = getWindow().getDecorView();
        tvDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tip("Activity.runOnUiThread 会直接将数据放入 looper," +
                        "View.post 会需要 AttachInfo ," +
                        "这个在 OnCreate 里面还没创建出来，所以真正执行的时候会靠后");
            }
        });
        final ProgressDialog dialog = provideDialog();
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hide();
                    }
                });
            }
        }).start();
    }

    private ProgressDialog mDialog;

    private ProgressDialog provideDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
//        dialog.setTitle("test dialog");
            mDialog.setMessage("runOnUiThread 会直接将数据放入 looper");
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
