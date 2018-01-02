package com.afirez.onactivityresult.demo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.afirez.onactivityresult.ActivityResult;
import com.afirez.onactivityresult.OnResultHelper;
import com.afirez.onactivityresult.R;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class MainActivity extends AppCompatActivity {

    private OnResultHelper onResultHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onResultHelper = new OnResultHelper(this);
    }

    public void onCallback(View view) {
        onResultHelper.startActivityForResult(FetchDataActivity.class, 17,
                new OnResultHelper.Callback() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                        if (Activity.RESULT_OK == resultCode) {
                            if (data != null) {
                                String text = data.getStringExtra("text");
                                Toast.makeText(MainActivity.this, "callback -> " + text, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "callback -> canceled", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    Disposable disposable;

    public void onRxJava(View view) {
        disposable = onResultHelper.startActivityForResult(FetchDataActivity.class, 18)
                .subscribe(new Consumer<ActivityResult>() {
                    @Override
                    public void accept(ActivityResult result) throws Exception {
                        int resultCode = result.getResultCode();
                        Intent data = result.getData();
                        int requestCode = result.getRequestCode();
                        if (Activity.RESULT_OK == resultCode && 18 == requestCode) {
                            if (data != null) {
                                String text = data.getStringExtra("text");
                                Toast.makeText(MainActivity.this, "RxJava -> " + text, Toast.LENGTH_SHORT).show();
                            }
                        } else if (Activity.RESULT_OK != resultCode && 18 == requestCode){
                            Toast.makeText(MainActivity.this, "RxJava -> canceled", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "normal -> " + text, Toast.LENGTH_SHORT).show();
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
