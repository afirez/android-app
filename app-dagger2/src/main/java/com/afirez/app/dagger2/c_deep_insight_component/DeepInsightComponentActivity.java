package com.afirez.app.dagger2.c_deep_insight_component;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.afirez.app.dagger2.R;

import java.util.Map;

public class DeepInsightComponentActivity extends AppCompatActivity {

    private static final String TAG = "DeepInsightComponentAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_insight_component);
        AComponent aComponent = DaggerAComponent.create();
        Map<Class<?>, String> classToString = aComponent.classToString();
        Map<String, Long> stringToLong = aComponent.stringToLong();
        Log.i(TAG, "onCreate: " + classToString);
        Log.i(TAG, "onCreate: " + stringToLong);
    }
}
