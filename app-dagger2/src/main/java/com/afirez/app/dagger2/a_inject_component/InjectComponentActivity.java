package com.afirez.app.dagger2.a_inject_component;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.afirez.app.dagger2.R;

import javax.inject.Inject;

import dagger.Lazy;

public class InjectComponentActivity extends AppCompatActivity {

    @Inject
    Lazy<User> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inject_component);
        InjectComponentComponent component = DaggerInjectComponentComponent.create();
        component.inject(this);
        Log.i("Dagger2", "user" + user.get());
        Log.i("Dagger2", "user" + component.userByLazy().get());
        Toast.makeText(this, "user.name = " + user.get().name, Toast.LENGTH_SHORT).show();
    }
}
