package com.gcml.common.uicall;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gcml.common.demo.R;
import com.gcml.common.utils.UiCall;

public class FmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fm);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void onUiCall(View view) {
        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
                UiCall.error(f);
            }
        }, true);
        UiCall.pushFragmentForResult(this, FirstFragment.class, new UiCall.ResultCallback() {
            @Override
            public void onResult(UiCall.Result result) {
                String msg = result.isSuccess() ? "成功" : "失败";
                tips(msg + "FirstFragment "  + result.requestCode);
            }
        });
    }

    public void tips(String msg) {
        Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
