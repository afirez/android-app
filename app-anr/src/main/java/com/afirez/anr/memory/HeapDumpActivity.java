package com.afirez.anr.memory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.afirez.anr.R;

public class HeapDumpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heap_dump);
    }
}
