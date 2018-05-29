package com.afirez.onactivityresult.demo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.afirez.onactivityresult.OnResultHelper;
import com.afirez.onactivityresult.R;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private OnResultHelper onResultHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onResultHelper = new OnResultHelper(this);
    }

    public void onCallback(View view) {
        onResultHelper.startActivityForResult(FetchDataActivity.class,
                new OnResultHelper.Callback() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                        if (Activity.RESULT_OK == resultCode) {
                            if (data != null) {
                                String text = data.getStringExtra("text");
                                Toast.makeText(MainActivity.this, requestCode + " callback -> " + text, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, requestCode + " callback -> canceled", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private Disposable disposable;

    public void onRxJava(View view) {
        if (disposable != null) {
            disposable.dispose();
        }
        disposable = onResultHelper.startActivityForResult(FetchDataActivity.class)
                .subscribe(new Consumer<OnResultHelper.Result>() {
                    @Override
                    public void accept(OnResultHelper.Result result) throws Exception {
                        int requestCode = result.getRequestCode();
                        int resultCode = result.getResultCode();
                        Intent data = result.getData();
                        if (Activity.RESULT_OK == resultCode) {
                            if (data != null) {
                                String text = data.getStringExtra("text");
                                Toast.makeText(MainActivity.this, requestCode + " RxJava -> " + text, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, requestCode + " RxJava -> canceled", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onNormal(View view) {
        Intent intent = new Intent(this, FetchDataActivity.class);
        startActivityForResult(intent, 16);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode && 16 == requestCode) {
            if (data != null) {
                String text = data.getStringExtra("text");
                Toast.makeText(this, requestCode + " normal -> " + text, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDestroy();
    }

}
