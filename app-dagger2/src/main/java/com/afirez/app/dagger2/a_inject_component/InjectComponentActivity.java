package com.afirez.app.dagger2.a_inject_component;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.afirez.app.dagger2.R;

import javax.inject.Inject;

public class InjectComponentActivity extends AppCompatActivity {

    @Inject
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inject_component);

        DaggerInjectComponentComponent.builder()
                .build()
                .inject(this);

        Log.i("Dagger2", "user.name = " + user.name);
        Toast.makeText(this, "user.name = " + user.name, Toast.LENGTH_SHORT).show();
    }
}
