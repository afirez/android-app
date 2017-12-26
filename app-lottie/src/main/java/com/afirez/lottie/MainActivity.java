package com.afirez.lottie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.OnCompositionLoadedListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        final View view = findViewById(R.id.cl_root);
        final LottieDrawable drawable = new LottieDrawable();
        LottieComposition.Factory.fromAssetFileName(this, "data.json", new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition composition) {
                drawable.setComposition(composition);
                drawable.setBounds(0, 0, 1200, 1920);
                drawable.setScale(1f);
                view.setBackground(drawable);
            }
        });
    }
}
