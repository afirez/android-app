package com.afirez.app.dagger2.b_module;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.afirez.app.dagger2.R;

import javax.inject.Inject;

public class ModuleActivity extends AppCompatActivity {

    @Inject
    Person mPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);

        //提供了无参构造函数的 Module 可以不设置
        DaggerModuleComponent.builder()
//                .moduleModule(new ModuleModule())
                .build()
                .inject(this);
        String text = "The one named afirez, who eat " + mPerson.mFruit.mName
                + " and address " + mPerson.mCloth.mName + " cloth,"
                + " is so cool";
        Log.d("Dagger", text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
